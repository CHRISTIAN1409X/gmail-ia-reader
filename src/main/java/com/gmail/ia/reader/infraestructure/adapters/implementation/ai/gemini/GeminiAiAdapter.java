package com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.cloude.PathPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.infraestructure.config.ai.GeminiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class GeminiAiAdapter {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAdapter.class);

    private static final String PROMPT_BASE = """
            Eres un asistente especializado en analizar planeadores académicos (PDF).
            Devuelve ÚNICAMENTE un objeto JSON sin markdown ni bloques de código.

            Se te proporcionan los siguientes documentos PDF en este orden:
            1. PLANEADOR - el planeador académico del estudiante
            """;

    private static final String PROMPT_WITH_MICRO = """
            2. MICRO-CURRÍCULO - el documento de referencia con los requisitos curriculares

            Evalúa los siguientes criterios COMPARANDO ambos documentos:
            1. "Contiene Firmas" - Verifica si el planeador contiene firmas del profesor y del representante estudiantil.
            2. "Fechas del planeador" - Verifica que las fechas del planeador sean coherentes y estén presentes.
            3. "Contenido de la unidad 1 corresponde con el micro-curriculum" - Compara el contenido de la unidad 1 del planeador contra el micro-currículo.
            4. "Las fechas de la unidad 4 están entre los límites de las fechas del planeador" - Verifica que las fechas de la unidad 4 estén dentro del rango del planeador.
            5. "Estructura del documento" - Verifica que el planeador tenga una estructura válida (encabezado, unidades, fechas, firmas).

            También determina la clasificación del documento en tres niveles basada en su contenido:
            - Nivel 1: Semestre académico en número romano (ej: "VIII", "IV", "I")
            - Nivel 2: Nombre completo de la materia (ej: "Etica profesional Ingeniero")
            - Nivel 3: Nombre del profesor (ej: "Profesor John Doe")

            Formato JSON requerido:
            {
              "criteriaResults": [
                { "criterion": "nombre del criterio", "passed": true, "observation": "observación", "score": 8 }
              ],
              "pathParts": [
                { "partPath": "nombre del nivel de carpeta 1" },
                { "partPath": "nombre del nivel de carpeta 2" },
                { "partPath": "nombre del nivel de carpeta 3" }
              ]
            }

            score debe ser un número del 1 al 10 (10 = máxima importancia).
            Si el criterio no se puede evaluar o no hay micro-currículo, usar passed=false y score=0.
            """;

    private static final String PROMPT_WITHOUT_MICRO = """
            No se proporcionó micro-currículo de referencia. Evalúa el planeador basándote en criterios generales.

            Evalúa los siguientes criterios:
            1. "Contiene Firmas" - Verifica si el documento contiene firmas del profesor y del representante estudiantil.
            2. "Fechas del planeador" - Verifica que las fechas del planeador sean coherentes y estén presentes.
            3. "Contenido de la unidad 1 corresponde con el micro-curriculum" - Evalúa si el contenido de la unidad 1 es coherente con un plan académico típico.
            4. "Las fechas de la unidad 4 están entre los límites de las fechas del planeador" - Verifica que las fechas de la unidad 4 estén dentro del rango del planeador.
            5. "Estructura del documento" - Verifica que el documento tenga una estructura de planeador válida (encabezado, unidades, fechas, firmas).

            También determina la clasificación del documento en tres niveles basada en su contenido:
            - Nivel 1: Semestre académico en número romano (ej: "VIII", "IV", "I")
            - Nivel 2: Nombre completo de la materia (ej: "Etica profesional Ingeniero")
            - Nivel 3: Nombre del profesor (ej: "Profesor John Doe")

            Formato JSON requerido:
            {
              "criteriaResults": [
                { "criterion": "nombre del criterio", "passed": true, "observation": "observación", "score": 8 }
              ],
              "pathParts": [
                { "partPath": "nombre del nivel de carpeta 1" },
                { "partPath": "nombre del nivel de carpeta 2" },
                { "partPath": "nombre del nivel de carpeta 3" }
              ]
            }

            score debe ser un número del 1 al 10 (10 = máxima importancia).
            Si el criterio no se puede evaluar, usar passed=false y score=0.
            """;

    private final RestClient restClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    public GeminiAiAdapter(RestClient geminiRestClient, GeminiConfig geminiConfig, ObjectMapper objectMapper) {
        this.restClient = geminiRestClient;
        this.geminiConfig = geminiConfig;
        this.objectMapper = objectMapper;
    }

    public IaRespondeRecord analyze(byte[] planeadorBytes, byte[] microcurriculumBytes, ParsedEmail email) {
        String base64Planeador = Base64.getEncoder().encodeToString(planeadorBytes);

        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(new GeminiRequest.Part(null, new GeminiRequest.InlineData("application/pdf", base64Planeador)));

        boolean hasMicrocurriculum = microcurriculumBytes != null && microcurriculumBytes.length > 0;
        if (hasMicrocurriculum) {
            String base64Micro = Base64.getEncoder().encodeToString(microcurriculumBytes);
            parts.add(new GeminiRequest.Part(null, new GeminiRequest.InlineData("application/pdf", base64Micro)));
        }

        String prompt = PROMPT_BASE + (hasMicrocurriculum ? PROMPT_WITH_MICRO : PROMPT_WITHOUT_MICRO);
        parts.add(new GeminiRequest.Part(prompt, null));

        GeminiRequest request = new GeminiRequest(
                List.of(new GeminiRequest.Content(parts))
        );

        long start = System.currentTimeMillis();
        GeminiResponse response;
        try {
            response = restClient.post()
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());
                        if (res.getStatusCode().value() == 429 || res.getStatusCode().value() == 403) {
                            throw new AiApiRetryableException("Gemini API rate limited or quota exceeded: " + body);
                        }
                        throw new AiApiException("Gemini API client error " + res.getStatusCode() + ": " + body);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());
                        throw new AiApiRetryableException("Gemini API server error " + res.getStatusCode() + ": " + body);
                    })
                    .body(GeminiResponse.class);
        } catch (AiApiException | AiApiRetryableException e) {
            throw e;
        } catch (Exception e) {
            throw new AiApiRetryableException("Gemini API request failed: " + e.getMessage(), e);
        }
        long elapsed = System.currentTimeMillis() - start;

        String text = extractText(response);
        AiParsedResponse parsed = parseJson(text);

        return new IaRespondeRecord(
                "Google Gemini",
                geminiConfig.getModel(),
                elapsed,
                LocalDateTime.now(),
                parsed.criteriaResults(),
                parsed.pathParts()
        );
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new AiApiException("Gemini returned empty response");
        }
        GeminiResponse.Candidate candidate = response.candidates().get(0);
        if (candidate.content() == null || candidate.content().parts() == null || candidate.content().parts().isEmpty()) {
            throw new AiApiException("Gemini response missing content parts");
        }
        String text = candidate.content().parts().get(0).text();
        if (text == null || text.isBlank()) {
            throw new AiApiException("Gemini response text is empty");
        }
        return text;
    }

    private AiParsedResponse parseJson(String text) {
        String json = text.replaceAll("(?s)```(?:json)?\\s*", "").trim();
        try {
            return objectMapper.readValue(json, AiParsedResponse.class);
        } catch (IOException e) {
            log.error("Failed to parse Gemini JSON response: {}", text, e);
            throw new AiApiException("Failed to parse AI response as JSON", e);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GeminiRequest(
            @JsonProperty("contents") List<Content> contents
    ) {
        public record Content(
                @JsonProperty("parts") List<Part> parts
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Part(
                @JsonProperty("text") String text,
                @JsonProperty("inlineData") InlineData inlineData
        ) {}

        public record InlineData(
                @JsonProperty("mimeType") String mimeType,
                @JsonProperty("data") String data
        ) {}
    }

    public record GeminiResponse(
            @JsonProperty("candidates") List<Candidate> candidates
    ) {
        public record Candidate(
                @JsonProperty("content") Content content
        ) {
            public record Content(
                    @JsonProperty("parts") List<Part> parts
            ) {
                public record Part(
                        @JsonProperty("text") String text
                ) {}
            }
        }
    }

    public record AiParsedResponse(
            @JsonProperty("criteriaResults") List<CriteriaResult> criteriaResults,
            @JsonProperty("pathParts") List<PathPart> pathParts
    ) {}
}
