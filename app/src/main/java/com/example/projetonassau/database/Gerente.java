package com.example.projetonassau.database;

public class Gerente {

    private String nome;
    private int idade;
    private boolean aluno;

    public Gerente(String nome, int idade, boolean aluno) {
        this.nome = nome;
        this.idade = idade;
        this.aluno = aluno;
    }

    public Gerente(){



    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public boolean isAluno() {
        return aluno;
    }

    public void setAluno(boolean aluno) {
        this.aluno = aluno;
    }
}
