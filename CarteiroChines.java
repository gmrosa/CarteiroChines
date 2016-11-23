import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Guilherme Murilo da Rosa & Plamedi Luzolo Lusembo
 */
public class CarteiroChines {
    private static int INFINITO = Integer.MAX_VALUE;
    private int tamanho;
    private int[][] matriz;
    private Set<Vertice> verticesGrauImpar = new LinkedHashSet<>();
    private Map<Aresta, CaminhoDijkstra> dijkstras = new LinkedHashMap<>();
    private Grafo grafo = new Grafo();

    public static void main(String[] args) {
	// XXX Usar infinito quando não houver ligação entre vertices
	// int[][] matriz = { //
	// { 0, 9, 10, INFINITO, INFINITO, 6 }, //
	// { 9, 0, 5, INFINITO, INFINITO, INFINITO }, //
	// { 10, 5, 0, 5, INFINITO, 14 }, //
	// { INFINITO, INFINITO, 5, 0, 3, 8 }, //
	// { INFINITO, INFINITO, INFINITO, 3, 0, 4 }, //
	// { 6, INFINITO, 14, 8, 4, 0 }, //
	// };
	int[][] matriz = { //
		{ 0, 12, 4, INFINITO, INFINITO, INFINITO, 3, INFINITO }, //
		{ 12, 0, 6, 10, INFINITO, 19, 3, INFINITO }, //
		{ 4, 6, 0, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, //
		{ INFINITO, 10, INFINITO, 0, INFINITO, 7, INFINITO, INFINITO }, //
		{ INFINITO, INFINITO, INFINITO, INFINITO, 0, 8, 6, 2 }, //
		{ INFINITO, 19, INFINITO, 7, 8, 0, 7, 3 }, //
		{ 3, 3, INFINITO, INFINITO, 6, 7, 0, INFINITO }, //
		{ INFINITO, INFINITO, INFINITO, INFINITO, 2, 3, INFINITO, 0 }, //
	};

	CarteiroChines carteiroChines = new CarteiroChines(matriz);
	System.out.println(carteiroChines.grafo.getCustoTotal());
	carteiroChines.executar();
	System.out.println(carteiroChines.grafo.getCustoTotal());
    }

    private CarteiroChines(int[][] matriz) {
	if (matriz != null && matriz.length > 0) {
	    tamanho = matriz.length;
	    if (tamanho == matriz[0].length) {
		this.matriz = matriz;
		criarMatriz();
	    } else {
		throw new IllegalArgumentException("A matriz deve ser uma matriz quadrada.");
	    }
	} else {
	    throw new IllegalArgumentException("Matriz inválida.");
	}
    }

    private void criarMatriz() {
	for (int i = 0; i < tamanho; i++) {
	    Vertice vOrigem = new Vertice(i);
	    grafo.addVertice(vOrigem);

	    for (int j = 0; j < tamanho; j++) {
		Vertice vDestino = new Vertice(j);
		int custo = matriz[i][j];
		if (i != j && custo < INFINITO) {
		    Aresta aresta = new Aresta(vOrigem, vDestino, custo);
		    grafo.addAresta(aresta);
		}
	    }
	}
    }

    private void executar() {
	executarDijkstraParaVerticesDeGrauImpar();
	duplicarCaminhosDeMenorCusto();
	imprimirCicloEuleriano();
    }

    private void executarDijkstraParaVerticesDeGrauImpar() {
	this.verticesGrauImpar = getVerticesGrauImpar();
	List<Vertice> verticesGrauImpar = new LinkedList<>();
	verticesGrauImpar.addAll(this.verticesGrauImpar);
	if (ehPar(verticesGrauImpar.size())) {
	    while (!verticesGrauImpar.isEmpty()) {
		Vertice vOrigem = verticesGrauImpar.remove(0);
		if (!verticesGrauImpar.isEmpty()) {
		    Dijkstra dijkstra = new Dijkstra(tamanho, matriz);
		    dijkstra.executar(vOrigem);
		    for (Vertice vDestino : verticesGrauImpar) {
			CaminhoDijkstra caminhoDijkstra = dijkstra.getCaminhoMinimo(vDestino);
			int custo = caminhoDijkstra.custoTotal;
			Aresta aresta = new Aresta(vOrigem, vDestino, custo);
			dijkstras.put(aresta, caminhoDijkstra);
		    }
		}
	    }
	} else {
	    throw new IllegalArgumentException("A quantidade de vértices de grau impar deve ser par.");
	}
    }

    private Set<Vertice> getVerticesGrauImpar() {
	Set<Vertice> verticesGrauImpar = new LinkedHashSet<>();
	for (Vertice vertice : grafo.estruturaGrafo.values()) {
	    int grau = vertice.arestas.size();
	    boolean temGrauImpar = !ehPar(grau);
	    if (temGrauImpar) {
		verticesGrauImpar.add(vertice);
	    }
	}
	return verticesGrauImpar;
    }

    private boolean ehPar(int grau) {
	return grau % 2 == 0;
    }

    private void duplicarCaminhosDeMenorCusto() {
	ConjuntoDeCaminhos permutacaoMenorCusto = getPermutacaoMenorCusto();
	for (CaminhoDijkstra caminho : permutacaoMenorCusto.conjunto) {
	    for (Aresta aresta : caminho.arestas) {
		grafo.duplicarAresta(aresta);
	    }
	}
    }

    private ConjuntoDeCaminhos getPermutacaoMenorCusto() {
	int custo = INFINITO;
	ConjuntoDeCaminhos resultado = null;
	for (ConjuntoDeCaminhos caminho : getPermutacaoDosCaminhos()) {
	    if (caminho.custoTotal < custo) {
		custo = caminho.custoTotal;
		resultado = caminho;
	    }
	}
	return resultado;
    }

    private List<ConjuntoDeCaminhos> getPermutacaoDosCaminhos() {
	Map<Aresta, CaminhoDijkstra> dijkstrasTemp = new LinkedHashMap<>(dijkstras);
	List<ConjuntoDeCaminhos> caminhos = new ArrayList<>();
	Set<Vertice> verticesGrauImparTemp = new LinkedHashSet<>();

	while (!dijkstrasTemp.isEmpty()) {
	    verticesGrauImparTemp.addAll(verticesGrauImpar);
	    ConjuntoDeCaminhos conjuntoDeCaminhos = new ConjuntoDeCaminhos();
	    while (!verticesGrauImparTemp.isEmpty()) {
		for (Map.Entry<Aresta, CaminhoDijkstra> entry : dijkstras.entrySet()) {
		    if (dijkstrasTemp.containsKey(entry.getKey())) {
			Aresta aresta = entry.getKey();
			if (aresta.vOrigem == verticesGrauImparTemp.iterator().next()) {
			    if (verticesGrauImparTemp.contains(aresta.vOrigem) && verticesGrauImparTemp.contains(aresta.vDestino)) {
				verticesGrauImparTemp.remove(aresta.vOrigem);
				verticesGrauImparTemp.remove(aresta.vDestino);
				CaminhoDijkstra caminhoDijkstra = dijkstrasTemp.remove(aresta);
				conjuntoDeCaminhos.addCaminho(caminhoDijkstra);
			    }
			}
		    }
		}
	    }
	    caminhos.add(conjuntoDeCaminhos);
	}
	return caminhos;
    }

    private void imprimirCicloEuleriano() {
	int custoTotal = 0;
	String resultado = "";
	Grafo grafoTemp = new Grafo();
	grafoTemp.estruturaGrafo = new LinkedHashMap<>(grafo.estruturaGrafo);

	while (!grafoTemp.estruturaGrafo.isEmpty()) {
	    Vertice vertice = grafoTemp.estruturaGrafo.values().iterator().next();
	    if (vertice.arestas.isEmpty()) {
		grafoTemp.estruturaGrafo.remove(vertice);
	    } else {
		Aresta aresta = vertice.arestas.remove(0);
		custoTotal += aresta.custo;
		resultado += aresta.vOrigem.nome + "" + aresta.custo + "" + aresta.vDestino.nome + " ";
	    }
	}
	resultado += " -> custoTotal=" + custoTotal;
	System.out.println(resultado);
    }

    private static class Grafo {
	private Map<Character, Vertice> estruturaGrafo = new LinkedHashMap<>();
	private int custoTotal = 0;
	private int custoDuplicadas = 0;

	private void addVertice(Vertice vertice) {
	    estruturaGrafo.put(vertice.nome, vertice);
	}

	private void duplicarAresta(Aresta aresta) {
	    List<Aresta> arestas = estruturaGrafo.get(aresta.vOrigem.nome).arestas;
	    Aresta arestaDuplicada = arestas.stream().filter(a -> a.equals(aresta)).findFirst().get();

	    custoDuplicadas += arestaDuplicada.custo;
	    arestas.add(arestaDuplicada);

	    Aresta outraAresta = new Aresta(aresta.vDestino, aresta.vOrigem);
	    arestas = estruturaGrafo.get(outraAresta.vOrigem.nome).arestas;
	    arestaDuplicada = arestas.stream().filter(a -> a.equals(outraAresta)).findFirst().get();

	    custoDuplicadas += arestaDuplicada.custo;
	    arestas.add(arestaDuplicada);
	}

	private void addAresta(Aresta aresta) {
	    custoTotal += aresta.custo;
	    estruturaGrafo.get(aresta.vOrigem.nome).arestas.add(aresta);
	}

	private int getCustoTotal() {
	    return custoTotal / 2 + custoDuplicadas;
	}

    }

    private static class ConjuntoDeCaminhos {
	private List<CaminhoDijkstra> conjunto = new LinkedList<>();
	private int custoTotal;

	private void addCaminho(CaminhoDijkstra caminho) {
	    custoTotal += caminho.custoTotal;
	    conjunto.add(caminho);
	}

	@Override
	public String toString() {
	    return "ConjuntoDeCaminhos [custoTotal=" + custoTotal + "]";
	}

    }

    private static class CaminhoDijkstra {
	private List<Aresta> arestas = new ArrayList<>();
	private int custoTotal;

	private CaminhoDijkstra(int custoTotal) {
	    this.custoTotal = custoTotal;
	}

	private void addAresta(Aresta aresta) {
	    arestas.add(aresta);
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    return "CaminhoDijkstra [caminho=" + arestas + ", custoTotal=" + custoTotal + "]";
	}

    }

    private static class Aresta {
	private Vertice vOrigem;
	private Vertice vDestino;
	private int custo;

	private Aresta(Vertice vOrigem, Vertice vDestino, int custo) {
	    this.vOrigem = vOrigem;
	    this.vDestino = vDestino;
	    this.custo = custo;
	}

	private Aresta(Vertice vOrigem, Vertice vDestino) {
	    this.vOrigem = vOrigem;
	    this.vDestino = vDestino;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((vDestino == null) ? 0 : vDestino.hashCode());
	    result = prime * result + ((vOrigem == null) ? 0 : vOrigem.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Aresta other = (Aresta) obj;
	    if (vDestino == null) {
		if (other.vDestino != null)
		    return false;
	    } else if (!vDestino.equals(other.vDestino))
		return false;
	    if (vOrigem == null) {
		if (other.vOrigem != null)
		    return false;
	    } else if (!vOrigem.equals(other.vOrigem))
		return false;
	    return true;
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    if (custo > 0) {
		return vOrigem.nome + "->" + vDestino.nome + "=" + custo;
	    }
	    return vOrigem.nome + "->" + vDestino.nome;
	}

    }

    private static class Dijkstra {
	private int tamanho;
	private int posicaoOrigem;
	private int[][] matriz;
	private String dijkstra[][];

	private Dijkstra(int tamanho, int[][] matriz) {
	    this.tamanho = tamanho;
	    this.matriz = matriz;
	    this.dijkstra = new String[3][tamanho];
	}

	private String[][] executar(Vertice vOrigem) {
	    posicaoOrigem = vOrigem.getPosicao();
	    inicializarDijkstra();
	    return gerarDijkstra();
	}

	private void inicializarDijkstra() {
	    for (int coluna = 0; coluna < tamanho; coluna++) {
		if (posicaoOrigem == coluna) {
		    dijkstra[0][coluna] = "0";
		} else {
		    dijkstra[0][coluna] = String.valueOf(INFINITO);
		}
	    }
	}

	private boolean naoAcabou(String dijkstra[][]) {
	    for (int coluna = 0; coluna < tamanho; coluna++) {
		if (dijkstra[2][coluna] == null) {
		    return true;
		}
	    }
	    return false;
	}

	private int getVerticeMenorCusto() {
	    int verticeMenorCusto = 0;
	    int menorCustoDisponivel = INFINITO;
	    for (int coluna = 0; coluna < tamanho; coluna++) {
		if (dijkstra[2][coluna] == null) {
		    int custo = Integer.parseInt(dijkstra[0][coluna]);
		    if (custo < menorCustoDisponivel) {
			menorCustoDisponivel = custo;
			verticeMenorCusto = coluna;
		    }
		}
	    }
	    return verticeMenorCusto;
	}

	private String[][] gerarDijkstra() {
	    while (naoAcabou(dijkstra)) {
		int verticeMenorCusto = getVerticeMenorCusto();

		for (int coluna = 0; coluna < tamanho; coluna++) {
		    if (dijkstra[2][coluna] == null && coluna != verticeMenorCusto) {
			int custoMatriz = matriz[verticeMenorCusto][coluna];
			int custoDijkstra = Integer.parseInt(dijkstra[0][coluna]);
			int custoVerticeMenorCusto = Integer.parseInt(dijkstra[0][verticeMenorCusto]);
			int custo = custoMatriz + custoVerticeMenorCusto;
			if (custoMatriz < custoDijkstra && custo < custoDijkstra) {
			    dijkstra[0][coluna] = String.valueOf(custo);
			    dijkstra[1][coluna] = String.valueOf(Vertice.getNome(verticeMenorCusto));
			}
		    }
		}
		dijkstra[2][verticeMenorCusto] = "x";
	    }
	    return dijkstra;
	}

	private CaminhoDijkstra getCaminhoMinimo(Vertice vDestino) {
	    Vertice vAtual = vDestino;
	    int custoTotal = Integer.parseInt(dijkstra[0][vAtual.getPosicao()]);

	    CaminhoDijkstra caminhoDijkstra = new CaminhoDijkstra(custoTotal);

	    while (dijkstra[1][vAtual.getPosicao()] != null) {
		Vertice verticeAnterior = vAtual;
		vAtual = new Vertice(dijkstra[1][verticeAnterior.getPosicao()].charAt(0));
		Aresta aresta = new Aresta(verticeAnterior, vAtual);
		caminhoDijkstra.addAresta(aresta);
	    }
	    return caminhoDijkstra;
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    String s = "";
	    for (int i = 0; i < tamanho; i++) {
		String custo = dijkstra[0][i];
		if (s.isEmpty()) {
		    s += "[" + custo;
		} else {
		    s += "," + custo;
		}

	    }
	    s += "]";
	    return s;
	}

    }

    private static class Vertice {
	private char nome;
	private List<Aresta> arestas = new LinkedList<>();

	private Vertice(int posicao) {
	    this.nome = getNome(posicao);
	}

	private Vertice(char nome) {
	    this.nome = nome;
	}

	private int getPosicao() {
	    return nome - 65;
	}

	private static char getNome(int posicao) {
	    return ((char) (65 + posicao));
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + nome;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Vertice other = (Vertice) obj;
	    if (nome != other.nome)
		return false;
	    return true;
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    return "[" + arestas + "]";
	}

    }

}
