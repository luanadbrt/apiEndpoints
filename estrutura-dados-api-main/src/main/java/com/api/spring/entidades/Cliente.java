package com.api.spring.entidades;

import com.api.spring.enums.TipoPessoa;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
public class Cliente {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Enumerated(EnumType.STRING)
    private TipoPessoa tipoPessoa;
    @Column(unique = true)
    private String cpfCnpj;
    private String telefone;
    private String email;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
