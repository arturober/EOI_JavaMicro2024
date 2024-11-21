package com.example.eventos.usuarios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eventos.eventos.Evento;
import com.example.eventos.usuarios.proyecciones.UsuarioSinEventos;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Integer>{
    List<UsuarioSinEventos> findBy();

    UsuarioSinEventos findUsuarioById(int id);
}
