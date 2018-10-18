import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static Map<Character, Boolean> fazerCorrespondencia(Map<Character, Boolean> valoracao, String expressao, String[] valores) {
        int indiceValor = 0;
        for (int i=0; i < expressao.length(); i++) {
            if (!(valoracao.containsKey(expressao.charAt(i))) && expressao.charAt(i) >= 'A' && expressao.charAt(i) <= 'Z') {
                if (valores[indiceValor].equals("0")) {
                    valoracao.put(expressao.charAt(i), false);
                } else {
                    valoracao.put(expressao.charAt(i), true);
                }
                indiceValor++;


            }
        }
        return valoracao;
    }

    static boolean satisfaz(Map<Character, Boolean> valoresVerdade, String expressao) {

        if (expressao.length() == 1) {
            return valoresVerdade.get(expressao.charAt(0));
        }

        int inicio = 0;
        int finale = expressao.length() - 1;
        int posOperador = acharPosOperador(expressao);

        switch (expressao.charAt(posOperador)) {
            case '~':
                return
                        !satisfaz(valoresVerdade, expressao.substring(posOperador + 1, finale));
            case '&':
                return
                        satisfaz(valoresVerdade, expressao.substring(inicio + 1, posOperador - 1))
                                && satisfaz(valoresVerdade, expressao.substring(posOperador + 2, finale));
            case 'v':
                return
                        satisfaz(valoresVerdade, expressao.substring(inicio + 1, posOperador - 1))
                                || satisfaz(valoresVerdade, expressao.substring(posOperador + 2, finale));
            case '>':
                return
                        !satisfaz(valoresVerdade, expressao.substring(inicio + 1, posOperador - 1))
                                || satisfaz(valoresVerdade, expressao.substring(posOperador + 2, finale));
            default:
                return true;
        }
    }

    static boolean ehLegitima(String expressao) {

        if (expressao.length() == 1 && expressao.charAt(0) >= 'A' && expressao.charAt(0) <= 'Z')
            return true;

        int inicio = 0;
        int finale = expressao.length() - 1;
        if (expressao.charAt(inicio) == '(' && expressao.charAt(finale) == ')' && expressao.charAt(inicio + 1) == '~' && inicio + 2 < finale) {
            return ehLegitima(expressao.substring(inicio + 2, finale));
        } else if (expressao.charAt(inicio) == '(' && expressao.charAt(finale) == ')') {
            int posOperador = acharPosOperador(expressao);
            if (posOperador != -1) {
                return ehLegitima(expressao.substring(inicio + 1, posOperador - 1)) && ehLegitima(expressao.substring(posOperador + 2, finale));
            }else{
                return false;
            }
        }

        return false;
    }

    static boolean temVariavelSeguida(String expressao) {
        int numVarSeguidas = 0;

        for (int i = 0; i < expressao.length(); i++) {
            if (expressao.charAt(i) >= 'A' && expressao.charAt(i) <= 'Z') {
                numVarSeguidas++;
                if (numVarSeguidas >= 2) {
                    return true;
                }
            }else{
                numVarSeguidas = 0;
            }
        }
        return false;
    }

    static int acharPosOperador(String expressao) {
        int numPar = 0;

        for (int i = 0; i < expressao.length(); i++) {
            if (expressao.charAt(i) == '(') {
                numPar++;
            } else if (expressao.charAt(i) == ')') {
                numPar--;
            }
            if ((expressao.charAt(i) == '>' || expressao.charAt(i) == '&' || expressao.charAt(i) == 'v') && numPar == 1) {
                return i;
            }
        }
        return -1;

    }

    public static void main(String[] args) throws IOException {

        // DEFININDO LEITURA DA ENTRADA
        FileReader entrada = new FileReader("Entrada.in");
        BufferedReader lerEntrada = new BufferedReader(entrada);

        // DEFININDO ARQ SAIDA
        FileWriter saida = new FileWriter("Saida.out");
        PrintWriter printarSaida = new PrintWriter(saida);

        // LEITURA DA ENTRADA E OPERACOES
        String numProblemasLeitura = lerEntrada.readLine();
        int numProblemas = Integer.parseInt(numProblemasLeitura);

        for (int i=0; i < numProblemas; i++) {
            String linha = lerEntrada.readLine();

            printarSaida.println("Problema #" + i);

            int indexAux = 0;
            for (int c = 0; c < linha.length(); c++) {
                if (linha.charAt(c) == '1' || linha.charAt(c) == '0') {
                    indexAux = c;
                    break;
                }
            }

            String part1 = linha.substring(0, indexAux);
            System.out.println(part1);
            String part2 = linha.substring(indexAux);
            String valores[] = part2.split(" ");

            Map<Character, Boolean> valoracaoAnt = new HashMap<>();
            Map<Character, Boolean> valoracao = fazerCorrespondencia(valoracaoAnt, part1, valores);

            // ~~~~ CASO SEJA UMA EXPRESSAO ISOLADA
            if (!(part1.charAt(0) == '{')) {
                System.out.println("eh sem chaves");

                if (!temVariavelSeguida(part1)) {
                    if (ehLegitima(part1)) {

                        // CHECA SE EH SATISFATIVEL
                        if (satisfaz(valoracao, part1)) {
                            printarSaida.println("A valoracao-verdade satisfaz a proposicao.");
                        } else {
                            printarSaida.println("A valoracao-verdade nao satisfaz a proposicao.");
                        }

                    } else {
                        printarSaida.println("A palavra nao e legitima.");
                    }
                }
            }

            // ~~~~ CASO SEJA UM CONJUNTO DE EXPRESSOES
            else {
                part1 = part1.substring(1, part1.length() - 2);
                System.out.println(part1);
                String expressoes[] = part1.split(", ");

                // CHECA SE TODAS AS EXPRESSOES SAO LEGITIMAS
                boolean todasLegitimas = true;
                for (String expressoe : expressoes) {
                    if (temVariavelSeguida(expressoe) && !(ehLegitima(expressoe))) {
                        todasLegitimas = false;
                        break;
                    }
                }

                if (todasLegitimas) {
                    boolean conjuntoSatisfativel = true;

                    // CHECA SE CADA EXPRESSAO É SATISFATIVEL
                    for (String expressoe : expressoes) {
                        if (!(satisfaz(valoracao, expressoe))) {
                            conjuntoSatisfativel = false;
                            break;
                        }
                    }

                    if (conjuntoSatisfativel) {
                        printarSaida.println("A valoracao-verdade satisfaz o conjunto.");
                    } else {
                        printarSaida.println("A valoracao-verdade nao satisfaz o conjunto");
                    }
                } else {
                    printarSaida.println("Ha uma palavra nao legitima no conjunto.");
                }
            }

            // PRINTA LINHAS VAZIAS QUE DIVIDEM AS SAIDAS
            if (i != numProblemas - 1) printarSaida.print("\n");

        }


        // FECHAR ARQUIVOS DE ENTRADA E SAIDA
        entrada.close();
        saida.close();

    }
}
