package com.api.spring.service;

import com.api.spring.entidades.Cliente;
import com.api.spring.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //injeta a dependencia de forma automatica
public class ClienteService {

    private final ClienteRepository clienteRepository;

/* METODO HARDCODED PARA INJECAO DE DEPENCIA NO CASO CLIENTE REPOSITORY
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

 */

    public Cliente salvarCliente(Cliente cliente) {

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes;
    }

    public Cliente buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id).
                orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado!"));
        return cliente;
    }

    public void deletarClientePorId(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente atualizarClientePorId(Long id, Cliente cliente) {
        Cliente clienteSalvo = buscarClientePorId(id);
        BeanUtils.copyProperties(cliente, clienteSalvo, "id");
        return clienteRepository.save(clienteSalvo);
    }
}
