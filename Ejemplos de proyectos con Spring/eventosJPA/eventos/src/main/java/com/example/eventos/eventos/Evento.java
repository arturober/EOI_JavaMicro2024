package com.example.eventos.eventos;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.Formula;

import com.example.eventos.eventos.dto.EventoDTO;
import com.example.eventos.usuarios.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titulo;
    private String descripcion;
    private double precio;
    private LocalDate fecha;

    @Formula("(select count(uae.usuario) from usuario_asiste_evento uae where uae.evento = id)")
    private int countUsuarios;

    @ManyToMany(mappedBy = "eventos")
    private List<Usuario> usuarios;

    static Evento fromDTO(EventoDTO eventoDTO) {
        return new Evento(0, eventoDTO.getTitulo(), eventoDTO.getDescripcion(), eventoDTO.getPrecio(),
                LocalDate.parse(eventoDTO.getFecha()), 0, null);
    }
}
