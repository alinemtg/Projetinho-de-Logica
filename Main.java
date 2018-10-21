import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    //  ~~~ METODO QUE PREENCHE O MAP COM AS VARIAVEIS E A CORRESPONDENTE VALORACAO DADA ~~~
    static Map<Character, Boolean> fazerCorrespondencia (String expressao, String[] valores) {

        Map<Character, Boolean> valoracao = new HashMap<>();
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

    // ~~~ METODO QUE CHECA SE A VALORACAO SATISFAZ A EXPRESSAO ~~
    static boolean satisfaz (Map<Character, Boolean> valoresVerdade, String expressao) {

        if (expressao.length() == 1) {
            return valoresVerdade.get(expressao.charAt(0));
        }

        int inicio = 0;
        int finale = expressao.length() - 1;
        int posOperador = -1;
        if (expressao.charAt(1) == '~'){
            posOperador = 1;
        }else{
            posOperador = acharPosOperador(expressao);
        }

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

    // ~~~ METODO QUE CHECA SE A EXPRESSAO EH LEGITIMA ~~~
    static boolean ehLegitima (String expressao) {

        if (expressao.length() == 0){
            return false;
        }

        if (expressao.length() == 1 && expressao.charAt(0) >= 'A' && expressao.charAt(0) <= 'Z')
            return true;

        int inicio = 0;
        int finale = expressao.length() - 1;
        if (expressao.charAt(inicio) == '(' && expressao.charAt(finale) == ')' && expressao.charAt(inicio + 1) == '~' && inicio + 2 < finale) {
            return ehLegitima(expressao.substring(inicio + 2, finale));
        } else if (expressao.charAt(inicio) == '(' && expressao.charAt(finale) == ')') {
            int posOperador = acharPosOperador(expressao);
            if (posOperador != -1 && posOperador-1>inicio) {
                return ehLegitima(expressao.substring(inicio + 1, posOperador - 1)) && ehLegitima(expressao.substring(posOperador + 2, finale));
            }else{
                return false;
            }
        }

        return false;
    }

    /* ~~ COMO O EH LEGITIMA NAO PEGA TODOS OS CASOS, TEM ESSE EXTRA QUE CHECA QUANDO A EXPRESSAO TEM VARIAVEIS SEGUIDAS, E CONSEQUENTEMENTE NAO EH LEGITIMA ~~~
    plus: editei e coloquei pra ver se tem operador seguido, mas por preguica ta o mesmo nome */
    static boolean temVariavelSeguida (String expressao) {

        int numVarSeguidas = 0;
        int numOpSeguidos = 0;

        for (int i = 0; i < expressao.length(); i++) {
            if (expressao.charAt(i) >= 'A' && expressao.charAt(i) <= 'Z') {
                numVarSeguidas++;
                if (numVarSeguidas >= 2) {
                    return true;
                }
            }else{
                numVarSeguidas = 0;
            }

            if (expressao.charAt(i) == '~' || expressao.charAt(i) == '>' || expressao.charAt(i) == '&' || expressao.charAt(i) == 'v'){
                numOpSeguidos++;
                if (numOpSeguidos >= 2) {
                    return true;
                }
            }else{
                numOpSeguidos = 0;
            }

        }
        return false;
    }

    // ~~~ METODO QUE RETORNA A POSICAO DO OPERADOR PRINCIPAL ~~~
    static int acharPosOperador (String expressao) {

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

    // ~~~~~~ MAINNN ~~~~~~~
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

        for (int i=0; i < numProblemas; i++) {  // FOR PARA LER CADA LINHA ISOLADAMENTE
            String linha = lerEntrada.readLine();

            printarSaida.println("Problema #" + (i+1));

            int indexAux = -1;
            for (int c = 0; c < linha.length(); c++) {  // INDEXAUX EH A POSICAO DE ONDE COMECA A VALORACAO
                if (linha.charAt(c) == '1' || linha.charAt(c) == '0') {
                    indexAux = c;
                    break;
                }
            }

            if (indexAux != -1) {   // SO SEGUIMOS ADIANTE CASO EXISTA VALORACAO NA LINHA LIDA
                String part1 = linha.substring(0, indexAux - 1);    // PART 1 = EXPRESSAO
                String part2 = linha.substring(indexAux);   // PART 2 = VALORACAO
                String valores[] = part2.split(" ");    // SPLIT NO ESPACO DA VALORACAO PARA CRIAR UM ARRAY COM OS VALORES

                // ~~~~ CASO SEJA UMA EXPRESSAO ISOLADA
                if (!(part1.contains("{"))) {

                    if (!(temVariavelSeguida(part1)) && ehLegitima(part1)) {    // SEGUE ADIANTE SE A EXPRESSAO EH LEGITIMA
                        Map<Character, Boolean> valoracao = fazerCorrespondencia (part1, valores);  // CRIA O MAP JA INSERINDO OS VALORES
                        // CHECA SE EH SATISFATIVEL
                            if (satisfaz(valoracao, part1)) {
                                printarSaida.println("A valoracao-verdade satisfaz a proposicao.");
                            } else {
                                printarSaida.println("A valoracao-verdade nao satisfaz a proposicao.");
                            }

                    } else {    // PRINTA Q NAO EH LEGITIMA CASO NAO SEJA
                            printarSaida.println("A palavra nao e legitima.");
                    }

                }

                // ~~~~ CASO SEJA UM CONJUNTO DE EXPRESSOES
                else {

                    part1 = part1.substring(1, part1.length() - 1);    // RETIRAR AS CHAVES
                    String expressoes[] = new String[1];    // DECLARA ARRAY PARA COLOCAR CADA EXPRESSAO NUMA POSICAO

                    if (part1.contains(", ")) {
                        expressoes = part1.split(", ");    // SPLIT NO ESPACO CASO TENHA MAIS DE UMA EXPRESSAO
                    } else {
                        expressoes[0] = part1;    // SE SO TIVER UMA, ALOCA ELA NA POSICAO 0
                    }

                    // CHECA SE TODAS AS EXPRESSOES SAO LEGITIMAS
                    boolean todasLegitimas = true;

                    for (String exp : expressoes) {
                        if (temVariavelSeguida(exp) || !(ehLegitima(exp))) {
                            todasLegitimas = false;
                            break;
                        }
                    }

                    if (todasLegitimas) {    // SEGUE ADIANTE SE TODAS SAO LEGITIMAS

                        Map<Character, Boolean> valoracao = fazerCorrespondencia (part1, valores);  // CRIA O MAP JA INSERINDO OS VALORES
                        // CHECA SE CADA EXPRESSAO É SATISFATIVEL
                        boolean conjuntoSatisfativel = true;
                        for (String exp : expressoes) {
                            if (!(satisfaz(valoracao, exp))) {
                                conjuntoSatisfativel = false;
                                break;
                            }
                        }

                        if (conjuntoSatisfativel) {
                            printarSaida.println("A valoracao-verdade satisfaz o conjunto.");
                        } else {
                            printarSaida.println("A valoracao-verdade nao satisfaz o conjunto.");
                        }

                    } else {    // PRINTA QUE NAO SAO TODAS LEGITIMAS CASO NAO SEJAM
                        printarSaida.println("Ha uma palavra nao legitima no conjunto.");
                    }

                }

            } else {  // CASO NAO EXISTA VALORACAO NA LINHA LIDA

                if (linha.contains("{")){
                    printarSaida.println("Ha uma palavra nao legitima no conjunto.");
                }else{
                    printarSaida.println("A palavra nao e legitima.");
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
