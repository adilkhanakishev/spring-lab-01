package kz.iitu.springlab.web;

import kz.iitu.springlab.aspect.SlowMethodAspect;
import kz.iitu.springlab.service.CatalogService;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab4")
public class CatalogController {

    private final CatalogService catalogService;
    private final SlowMethodAspect slowMethodAspect;

    public CatalogController(CatalogService catalogService, SlowMethodAspect slowMethodAspect) {
        this.catalogService = catalogService;
        this.slowMethodAspect = slowMethodAspect;
    }

    @GetMapping("/item/{id}")
    public Map<String, String> getItem(@PathVariable("id") long id) {
        return Map.of("result", catalogService.findById(id));
    }

    @GetMapping("/items")
    public Map<String, Object> getItems(@RequestParam(name = "limit", defaultValue = "5") int limit) {
        return Map.of(
                "limit", limit,
                "items", catalogService.findAll(limit)
        );
    }

    @DeleteMapping("/item/{id}")
    public Map<String, String> removeItem(@PathVariable("id") long id) {
        return Map.of("result", catalogService.remove(id));
    }

    @GetMapping("/proxy")
    public Map<String, String> proxyInfo() {
        return Map.of(
                "className", catalogService.getClass().getName(),
                "superClass", catalogService.getClass().getSuperclass().getSimpleName(),
                "isAopProxy", String.valueOf(AopUtils.isAopProxy(catalogService)),
                "isCglib", String.valueOf(AopUtils.isCglibProxy(catalogService))
        );
    }

    @GetMapping("/remove-twice/{id}")
    public Map<String, String> removeTwice(@PathVariable("id") long id) {
        return Map.of(
                "note", "Executed via self-invocation (this.remove), bypassing proxy",
                "result", catalogService.removeTwice(id)
        );
    }

    @GetMapping("/remove-twice-fixed/{id}")
    public Map<String, String> removeTwiceFixed(@PathVariable("id") long id) {
        return Map.of(
                "note", "Executed via injected self proxy (self.remove), intercepting each call",
                "result", catalogService.removeTwiceFixed(id)
        );
    }

    @GetMapping("/slow-methods")
    public List<SlowMethodAspect.SlowMethodRecord> getSlowMethods() {
        return slowMethodAspect.getSlowMethods();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "status", "400"
        );
    }
}
