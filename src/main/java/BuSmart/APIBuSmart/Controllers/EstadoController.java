package BuSmart.APIBuSmart.Controllers;

import BuSmart.APIBuSmart.Exceptions.ExcepUsuarios.ExcepcionDatosDuplicados;
import BuSmart.APIBuSmart.Exceptions.ExcepUsuarios.ExceptionRutaNoEncontrada;
import BuSmart.APIBuSmart.Models.DTO.EstadoDTO;
import BuSmart.APIBuSmart.Models.DTO.RutaDTO;
import BuSmart.APIBuSmart.Service.EstadoService;
import BuSmart.APIBuSmart.Service.RutaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiEstado")
public class EstadoController {

    @Autowired
    EstadoService service;

    @GetMapping("/ConsultarEstado")
    public List<EstadoDTO> obtenerDatos(){return  service.obtenerEstado();}


    @PostMapping("/RegistrarEstado")
    public ResponseEntity<?> nuevoEstado(@Valid @RequestBody EstadoDTO json, HttpServletRequest request){
        try{
            EstadoDTO respuesta = service.InsertarEstado(json);
            if (respuesta == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Inserccion fallida",
                        "errorType", "VALIDACION_ERROR",
                        "message", "Los Estado no pudieron ser registrados"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Success",
                    "data", respuesta
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "Error",
                            "message", "Error no controlado al regitrar Estado",
                            "detail", e.getMessage()
                    ));
        }

    }

    @PutMapping("/EditarEstado/{id}")
    public ResponseEntity<?> modificarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoDTO json,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()){
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try{
            EstadoDTO dto = service.ActualizarEstado(id, json);
            return ResponseEntity.ok(dto);
        }catch (ExceptionRutaNoEncontrada e){
            return ResponseEntity.notFound().build();
        }catch (ExcepcionDatosDuplicados e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("Error", "Datos duplicados",
                            "Campo", e.getCampoDuplicado())
            );
        }
    }


    @DeleteMapping("/EliminarEstado/{id}")
    public  ResponseEntity<?> EliminarEstado(
            @PathVariable Long id)
    {
        try{
            if (!service.QuitarEstado(id)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Mensaje de error", "Estado no Encontrada")
                        .body(Map.of(
                                "Error", "NOT FOUND",
                                "Mensaje", "La Estado no fue encontrada",
                                "timestamp", Instant.now().toString()
                        ));
            }
            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",
                    "message", "Estado eliminada exitosamente"
            ));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",
                    "message", "Error al eliminar la Estado",
                    "detail", e.getMessage()
            ));
        }
    }

}
