package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CompilationCreateDTO;
import ru.practicum.main.dto.CompilationDTO;
import ru.practicum.main.dto.CompilationPatchDTO;
import ru.practicum.main.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDTO createCompilation(@RequestBody CompilationCreateDTO compilationCreateDTO) {
        log.debug("createCompilation: {}", compilationCreateDTO);
        return compilationService.createCompilation(compilationCreateDTO);
    }

    @PatchMapping("/admin/compilations/{id}")
    public CompilationDTO editCompilation(@RequestBody CompilationPatchDTO compilationPatchDTO,
                                          @PathVariable Long id) {
        log.debug("editCompilation: compilationPatchDTO=[{}], id={}", compilationPatchDTO, id);
        return compilationService.editCompilation(compilationPatchDTO, id);
    }

    @DeleteMapping("/admin/compilations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        log.debug("deleteCompilation: {}", id);
        compilationService.deleteCompilation(id);
    }

    @GetMapping("/compilations/{id}")
    public CompilationDTO getCompilationById(@PathVariable Long id) {
        log.debug("getCompilationById: {}", id);
        return compilationService.getCompilationById(id);
    }

    @GetMapping("/compilations")
    public List<CompilationDTO> getCompilations(@RequestParam(required = false) Boolean pinned,
                                               @RequestParam(required = false, defaultValue = "0") Long from,
                                               @RequestParam(required = false, defaultValue = "10") Long size) {
        log.debug("getCompilations: pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }
}
