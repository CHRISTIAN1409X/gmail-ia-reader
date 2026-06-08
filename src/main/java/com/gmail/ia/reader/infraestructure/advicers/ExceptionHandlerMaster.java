package com.gmail.ia.reader.infraestructure.advicers;


import com.gmail.ia.reader.infraestructure.advicers.exceptions.BadRequestException;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.ResourceAlreadyExists;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.ResourceNotFound;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.ResourceUnauthorized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerMaster {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFound.class})
    @ResponseBody
    public ErrorMessage resourceNotFound(Exception e){
        return new ErrorMessage(e,HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({ResourceAlreadyExists.class})
    public ResponseEntity<ErrorMessage> conflictCallingResource(Exception e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body((new ErrorMessage(e,HttpStatus.CONFLICT.value())));
    }

    @ExceptionHandler({ResourceUnauthorized.class})
    public ResponseEntity<ErrorMessage> unauthorizedResource(Exception e ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(e,HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorMessage> badRequest(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e,HttpStatus.BAD_REQUEST.value()));
    }
}
