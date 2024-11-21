package com.example.eventos.eventos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.eventos.eventos.dto.EventoDTO;
import com.example.eventos.usuarios.Usuario;
import com.example.eventos.usuarios.UsuariosRepository;
import com.example.eventos.utils.ImageUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventosService {
    private final @NonNull EventosRepository eventosRepository;
    private final @NonNull UsuariosRepository usuariosRepository;
    private final @NonNull ImageUtils imageUtils;

    public List<Evento> getAll() {
        return eventosRepository.findBy();
    }

    public Evento getById(int id) {
        Evento e = eventosRepository.findEventoById(id);
        return e;
    }

    public Evento insert(EventoDTO eventoDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idAuth = Integer.parseInt(auth.getCredentials().toString());
        Usuario usuario = new Usuario();
        usuario.setId(idAuth);

        eventoDTO.setImagen(imageUtils.saveImageBase64("eventos", eventoDTO.getImagen()));
        Evento evento = eventosRepository.save(Evento.fromDTO(eventoDTO, usuario));
        return eventosRepository.findEventoById(evento.getId());
    }

    public Evento update(int id, EventoDTO eventoDTO) {
        Evento evento = getAndCheckEvento(id);

        if (!eventoDTO.getImagen().startsWith("http")) { // La imagen viene en Base64
            eventoDTO.setImagen(imageUtils.saveImageBase64("eventos", eventoDTO.getImagen()));
        }
        evento = Evento.fromDTO(eventoDTO, evento.getCreador());
        evento.setId(id);
        eventosRepository.save(evento);
        return eventosRepository.findEventoById(evento.getId());
    }

    public void delete(int idEvento) {
        Evento evento = getAndCheckEvento(idEvento);
        eventosRepository.delete(evento);
    }

    public void asistirEvento(int idEvento) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idAuth = (Integer)auth.getCredentials();

        Usuario usuario = usuariosRepository.findById(idAuth).get();

        Evento evento = eventosRepository.findById(idEvento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    
        if(evento.getUsuarios().contains(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya asiste a este evento");
        }
        evento.getUsuarios().add(usuario);
        usuario.getEventos().add(evento);
        eventosRepository.flush();
    }

    public void borraAsistencia(int idEvento) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idAuth = Integer.parseInt(auth.getCredentials().toString());

        Usuario usuario = usuariosRepository.findById(idAuth).get();

        Evento evento = eventosRepository.findById(idEvento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    
        usuario.getEventos().remove(evento);
        evento.getUsuarios().remove(usuario);
        eventosRepository.flush();
    }

    public List<Usuario> getAsistentesEvento(int idEvento) {
        Evento evento = eventosRepository.findById(idEvento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    
        return evento.getUsuarios();
    } 

    private Evento getAndCheckEvento(int id) {
        Evento evento = eventosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idAuth = Integer.parseInt(auth.getCredentials().toString());
        if(evento.getCreador().getId() != idAuth) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El evento no es tuyo");
        }
        return evento;
    }
}
