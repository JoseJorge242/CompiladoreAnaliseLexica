package AnalisadorSemantico;

import java.util.*;

public class TabelaDeSimbolos {

    private final Deque<Map<String, Simbolo>> pilha = new ArrayDeque<>();

    public void entrarEscopo() {
        pilha.push(new LinkedHashMap<>());
    }

    public void sairEscopo() {
        if (!pilha.isEmpty()) pilha.pop();
    }

    public int profundidade() {
        return pilha.size();
    }

    public void declarar(Simbolo simbolo) {
        Map<String, Simbolo> escopoAtual = pilha.peek();
        if (escopoAtual == null)
            throw new RuntimeException("Nenhum escopo ativo. Chame entrarEscopo() primeiro.");

        if (escopoAtual.containsKey(simbolo.getNome())) {
            Simbolo existente = escopoAtual.get(simbolo.getNome());
            throw new ErroSemantico(
                simbolo.getLinha(), simbolo.getColuna(),
                "Variável '" + simbolo.getNome() + "' já foi declarada neste escopo "
                + "(declarada na linha " + existente.getLinha() + ")");
        }
        escopoAtual.put(simbolo.getNome(), simbolo);
    }

    public Simbolo buscar(String nome) {
        for (Map<String, Simbolo> escopo : pilha) {
            if (escopo.containsKey(nome))
                return escopo.get(nome);
        }
        return null;
    }

    public Map<String, Simbolo> escopoAtual() {
        return pilha.isEmpty() ? Collections.emptyMap() : pilha.peek();
    }
}