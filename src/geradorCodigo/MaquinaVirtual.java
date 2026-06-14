package geradorCodigo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MaquinaVirtual {
	public static class InstrucaoVM {
        public Opcode opcode;
        public String argumento;

        public InstrucaoVM(Opcode opcode, String argumento) {
            this.opcode = opcode;
            this.argumento = argumento;
        }

        @Override
        public String toString() {
            return opcode + (argumento != null ? " " + argumento : "");
        }
    }

    public List<InstrucaoVM> traduzirParaBytecode(List<String> codigoTAC) {
        List<InstrucaoVM> programaBytecode = new ArrayList<>();

        for (String linha : codigoTAC) {
            linha = linha.trim();
            if (linha.isEmpty()) continue;

            if (linha.endsWith(":")) {
                String labelNome = linha.substring(0, linha.length() - 1).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.LABEL, labelNome));
                continue;
            }

            if (linha.startsWith("goto ")) {
                String labelDestino = linha.substring(5).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.JUMP, labelDestino));
                continue;
            }

            if (linha.startsWith("print ")) {
                String arg = linha.substring(6).trim();
                if (arg.startsWith("\"")) {
                    programaBytecode.add(new InstrucaoVM(Opcode.PRINT_STR, arg));
                } else {
                    empilharOperando(arg, programaBytecode);
                    programaBytecode.add(new InstrucaoVM(Opcode.PRINT, null));
                }
                continue;
            }

            if (linha.startsWith("read ")) {
                String varNome = linha.substring(5).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.READ, varNome));
                continue;
            }

            if (linha.startsWith("if ") || linha.startsWith("ifFalse ")) {
                boolean isIf = linha.startsWith("if ");
                String[] partes = linha.split(" goto ");
                String labelDestino = partes[1].trim();
                String condicao = partes[0].substring(isIf ? 3 : 8).trim();

                String[] tokens = condicao.split("\\s+");
                
                if (tokens.length == 1) {
                    empilharOperando(tokens[0], programaBytecode);
                } else if (tokens.length == 3) {
                    empilharOperando(tokens[0], programaBytecode);
                    empilharOperando(tokens[2], programaBytecode);
                    
                    switch (tokens[1]) {
                        case "==": programaBytecode.add(new InstrucaoVM(Opcode.CMP_EQ, null)); break;
                        case "!=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_NE, null)); break;
                        case "<":  programaBytecode.add(new InstrucaoVM(Opcode.CMP_LT, null)); break;
                        case "<=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LE, null)); break;
                        case ">":  programaBytecode.add(new InstrucaoVM(Opcode.CMP_GT, null)); break;
                        case ">=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GE, null)); break;
                    }
                }

                if (isIf) {
                    programaBytecode.add(new InstrucaoVM(Opcode.JIF_TRUE, labelDestino));
                } else {
                    programaBytecode.add(new InstrucaoVM(Opcode.JIF_FALSE, labelDestino));
                }
                continue;
            }

            if (linha.contains(" = ")) {
                String[] partes = linha.split(" = ");
                String destino = partes[0].trim();
                String expressao = partes[1].trim();

                String[] tokens = expressao.split("\\s+");

                if (tokens.length == 1) {
                    empilharOperando(tokens[0], programaBytecode);
                } 
                else if (tokens.length == 2) {
                    empilharOperando(tokens[1], programaBytecode);
                    if (tokens[0].equals("-")) {
                        programaBytecode.add(new InstrucaoVM(Opcode.NEG, null));
                    } else if (tokens[0].equals("!")) {
                        programaBytecode.add(new InstrucaoVM(Opcode.NOT, null));
                    }
                } 
                else if (tokens.length == 3) {
                    empilharOperando(tokens[0], programaBytecode);
                    empilharOperando(tokens[2], programaBytecode);

                    switch (tokens[1]) {
                        case "+":  programaBytecode.add(new InstrucaoVM(Opcode.ADD, null)); break;
                        case "-":  programaBytecode.add(new InstrucaoVM(Opcode.SUB, null)); break;
                        case "*":  programaBytecode.add(new InstrucaoVM(Opcode.MUL, null)); break;
                        case "/":  programaBytecode.add(new InstrucaoVM(Opcode.DIV, null)); break;
                        case "%":  programaBytecode.add(new InstrucaoVM(Opcode.MOD, null)); break;
                        case "&&": programaBytecode.add(new InstrucaoVM(Opcode.AND, null)); break;
                        case "||": programaBytecode.add(new InstrucaoVM(Opcode.OR, null)); break;
                        case "==": programaBytecode.add(new InstrucaoVM(Opcode.CMP_EQ, null)); break;
                        case "!=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_NE, null)); break;
                        case "<":  programaBytecode.add(new InstrucaoVM(Opcode.CMP_LT, null)); break;
                        case "<=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LE, null)); break;
                        case ">":  programaBytecode.add(new InstrucaoVM(Opcode.CMP_GT, null)); break;
                        case ">=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GE, null)); break;
                    }
                }

                programaBytecode.add(new InstrucaoVM(Opcode.STORE, destino));
            }
        }

        programaBytecode.add(new InstrucaoVM(Opcode.HALT, null));
        return programaBytecode;
    }
    public void executar(List<GeradorByteCode.InstrucaoVM> programa) {
        Map<String, Integer> tabelaLabels = new HashMap<>();
        for (int i = 0; i < programa.size(); i++) {
            if (programa.get(i).opcode == Opcode.LABEL) {
                tabelaLabels.put(programa.get(i).argumento, i);
            }
        }

        Deque<Double> pilha = new ArrayDeque<>();
        Map<String, Double> memoria = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int pc = 0; // Program Counter

        while (pc < programa.size()) {
            GeradorByteCode.InstrucaoVM ins = programa.get(pc);

            switch (ins.opcode) {
                case PUSH:
                    pilha.push(Double.parseDouble(ins.argumento));
                    break;

                case LOAD:
                    Double val = memoria.get(ins.argumento);
                    pilha.push(val != null ? val : 0.0);
                    break;

                case STORE:
                    memoria.put(ins.argumento, pilha.pop());
                    break;

                case ADD: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a + b); break; }
                case SUB: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a - b); break; }
                case MUL: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a * b); break; }
                case DIV: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a / b); break; }
                case MOD: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a % b); break; }

                case NEG: pilha.push(-pilha.pop()); break;
                case NOT: pilha.push(pilha.pop() == 0.0 ? 1.0 : 0.0); break;
                case AND: { double b = pilha.pop(), a = pilha.pop(); pilha.push((a != 0 && b != 0) ? 1.0 : 0.0); break; }
                case OR:  { double b = pilha.pop(), a = pilha.pop(); pilha.push((a != 0 || b != 0) ? 1.0 : 0.0); break; }

                case CMP_EQ: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a == b ? 1.0 : 0.0); break; }
                case CMP_NE: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a != b ? 1.0 : 0.0); break; }
                case CMP_LT: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a <  b ? 1.0 : 0.0); break; }
                case CMP_LE: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a <= b ? 1.0 : 0.0); break; }
                case CMP_GT: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a >  b ? 1.0 : 0.0); break; }
                case CMP_GE: { double b = pilha.pop(), a = pilha.pop(); pilha.push(a >= b ? 1.0 : 0.0); break; }

                case LABEL: break; // Ignorado na execução

                case JUMP:
                    pc = tabelaLabels.get(ins.argumento);
                    continue;

                case JIF_TRUE:
                    if (pilha.pop() != 0.0) { pc = tabelaLabels.get(ins.argumento); continue; }
                    break;

                case JIF_FALSE:
                    if (pilha.pop() == 0.0) { pc = tabelaLabels.get(ins.argumento); continue; }
                    break;

                case READ:
                    System.out.print("Entrada para '" + ins.argumento + "': ");
                    memoria.put(ins.argumento, scanner.nextDouble());
                    break;

                case PRINT:
                    double resultado = pilha.pop();
                    // Imprime sem decimal se for inteiro
                    System.out.println((resultado == Math.floor(resultado))
                        ? String.valueOf((long) resultado)
                        : String.valueOf(resultado));
                    break;

                case PRINT_STR:
                    // Remove as aspas da string literal
                    System.out.println(ins.argumento.replace("\"", ""));
                    break;

                case HALT:
                    scanner.close();
                    return;
            	}
            pc++;
        	}
        }
    private void empilharOperando(String token, List<InstrucaoVM> programa) {
        if (token.equals("true")) {
            programa.add(new InstrucaoVM(Opcode.PUSH, "1"));
        } else if (token.equals("false")) {
            programa.add(new InstrucaoVM(Opcode.PUSH, "0"));
        } else if (token.matches("-?\\d+(\\.\\d+)?")) {
            programa.add(new InstrucaoVM(Opcode.PUSH, token));
        } else {
            programa.add(new InstrucaoVM(Opcode.LOAD, token));
        }
    }
}
