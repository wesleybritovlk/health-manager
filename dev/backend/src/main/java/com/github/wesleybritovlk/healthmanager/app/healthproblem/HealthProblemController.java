package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static com.github.wesleybritovlk.healthmanager.common.CommonResource.toResource;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;
import com.github.wesleybritovlk.healthmanager.common.CommonController;
import com.github.wesleybritovlk.healthmanager.handler.GlobalHandlerDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "health problem", description = "Access to health problems")
public interface HealthProblemController extends CommonController<Request, Response> {

    @Operation(summary = "Add a new health problem")
    @ApiResponse(responseCode = "201", description = "Health problem created successfully!", content = @Content(schema = @Schema(type = "object", properties = {
            @StringToClassMapItem(key = "message", value = String.class),
            @StringToClassMapItem(key = "content", value = Object.class)
    })))
    @ApiResponse(responseCode = "404", description = "Customer not found, please check the id", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(ref = "HealthProblemRequest", implementation = Request.class)))
    ResponseEntity<Map<Object, Object>> create(Request request);

    @Operation(summary = "Get health problem by id")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Path to find health problem by id", required = true, schema = @Schema(type = "string", format = "uuid", example = "health problem uuid"))
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(type = "object", properties = {
            @StringToClassMapItem(key = "content", value = HealthProblemDTO.Response.class)
    })))
    @ApiResponse(responseCode = "404", description = "Health Problem not found, please check the id", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    ResponseEntity<Map<Object, Object>> getById(UUID id);

    @Operation(summary = "Returns all paginated health problems")
    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Query to set current page number", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Query to limit health problems", required = false, schema = @Schema(type = "integer", example = "10"))
    ResponseEntity<Page<Response>> getAll(Integer pageNumber, Integer pageSize);

    @Operation(summary = "Put health problem by id")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Path to find and update health problem by id", required = true, schema = @Schema(type = "string", format = "uuid", example = "health problem uuid"))
    @ApiResponse(responseCode = "200", description = "Health problem updated successfully!", content = @Content(schema = @Schema(type = "object", properties = {
            @StringToClassMapItem(key = "message", value = String.class),
            @StringToClassMapItem(key = "content", value = Object.class)
    })))
    @ApiResponse(responseCode = "404", description = "Health Problem not found, please check the id", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found, please check the id", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    @ApiResponse(responseCode = "409", description = "This Health Problem already exists in this Customer", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(ref = "HealthProblemRequest", implementation = Request.class)))
    ResponseEntity<Map<Object, Object>> update(UUID id, Request request);

    @Operation(summary = "Delete health problem by id")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Path to delete health problem by id", required = true, schema = @Schema(type = "string", format = "uuid", example = "health problem uuid"))
    @ApiResponse(responseCode = "200", description = "Health problem deleted successfully!", content = @Content(schema = @Schema(type = "object", properties = {
            @StringToClassMapItem(key = "message", value = String.class),
            @StringToClassMapItem(key = "content", value = Object.class)
    })))
    @ApiResponse(responseCode = "404", description = "Health Problem not found, please check the id", content = @Content(schema = @Schema(ref = "GlobalHandlerResponse", implementation = GlobalHandlerDTO.class)))
    ResponseEntity<Map<Object, Object>> delete(UUID id);
}

@RestController
@RequestMapping("api/health-problems")
@RequiredArgsConstructor
class HealthProblemControllerImpl implements HealthProblemController {
    private final HealthProblemService service;

    @Override
    @PostMapping
    public ResponseEntity<Map<Object, Object>> create(@Valid @RequestBody Request request) {
        var response = service.create(request);
        var resource = toResource("Health problem created successfully!", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity<Map<Object, Object>> getById(@PathVariable UUID id) {
        var response = service.findById(id);
        var resource = toResource(response);
        return ResponseEntity.ok(resource);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<Response>> getAll(
            @RequestParam(name = "page", required = false) Integer pageNumber,
            @RequestParam(name = "size", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize != null ? pageSize : 10);
        Page<Response> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<Map<Object, Object>> update(@PathVariable UUID id, @Valid @RequestBody Request request) {
        var response = service.update(id, request);
        var resource = toResource("Health problem updated successfully!", response);
        return ResponseEntity.ok(resource);
    }

    @Override
    @DeleteMapping("{id}")
    public ResponseEntity<Map<Object, Object>> delete(@PathVariable UUID id) {
        var response = service.delete(id);
        var resource = toResource("Health problem deleted successfully!", response);
        return ResponseEntity.ok(resource);
    }
}
