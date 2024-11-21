package com.example.eventos.eventos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.events.Event;

import com.example.eventos.eventos.dto.EventoDTO;
import com.example.eventos.eventos.proyecciones.EventoSinUsuarios;
import com.example.eventos.usuarios.UsuariosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventosService {
    private final EventosRespository eventosRespository;
    private final UsuariosRepository usuariosRepository;

    public List<EventoSinUsuarios> getAll() {
        return eventosRespository.findBy();
    }

    public EventoSinUsuarios getById(int id) {
        EventoSinUsuarios e = eventosRespository.findEventoById(id);
        return e;
    }

    public EventoSinUsuarios insert(EventoDTO eventoDTO) {
        Evento evento = eventosRespository.save(Evento.fromDTO(eventoDTO));
        return eventosRespository.findEventoById(evento.getId());
    }

    public EventoSinUsuarios update(int id, EventoDTO eventoDTO) {
        if (!eventosRespository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }

        Evento evento = Evento.fromDTO(eventoDTO); 
        evento.setId(id);
        eventosRespository.save(evento);
        return eventosRespository.findEventoById(evento.getId());
    }

    public void delete(int idEvento) {
        eventosRespository.deleteById(idEvento);
    }
}
