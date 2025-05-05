package ru.practicum.main.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.Compilation;
import ru.practicum.main.dto.CompilationCreateDTO;
import ru.practicum.main.dto.CompilationDTO;
import ru.practicum.main.dto.EventDTO;

import java.util.List;

@Service
public class CompilationMapper {
    public CompilationDTO toCompilationDTO(Compilation compilation, List<EventDTO> events) {
        return new CompilationDTO(events, compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }

    public Compilation toCompilation(CompilationCreateDTO compilationCreateDTO) {
        return new Compilation(null, compilationCreateDTO.getPinned(), compilationCreateDTO.getTitle());
    }
}
