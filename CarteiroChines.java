import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Guilherme Murilo da Rosa & Plamedi Luzolo Lusembo
 */
public class CarteiroChines {
    private static int INFINITO = Integer.MAX_VALUE;
    private int custoOriginal;
    private int custoAdicional;
    private int tamanho;
    private int[][] matriz;
    private Set<Vertice> verticesGrauImpar = new LinkedHashSet<>();
    private Map<Aresta, CaminhoDijkstra> dijkstras = new LinkedHashMap<>();
    private Grafo grafo = new Grafo();

    public static void main(String[] args) {
	// XXX Usar infinito quando não houver ligação entre vertices
	int[][] matriz = { //
		{ 0, 9, 10, INFINITO, INFINITO, 6 }, //
		{ 9, 0, 5, INFINITO, INFINITO, INFINITO }, //
		{ 10, 5, 0, 5, INFINITO, 14 }, //
		{ INFINITO, INFINITO, 5, 0, 3, 8 }, //
		{ INFINITO, INFINITO, INFINITO, 3, 0, 4 }, //
		{ 6, INFINITO, 14, 8, 4, 0 }, //
	};
	// int[][] matriz = { //
	// { 0, 12, 4, INFINITO, INFINITO, INFINITO, 3, INFINITO }, //
	// { 12, 0, 6, 10, INFINITO, 19, 3, INFINITO }, //
	// { 4, 6, 0, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, //
	// { INFINITO, 10, INFINITO, 0, INFINITO, 7, INFINITO, INFINITO }, //
	// { INFINITO, INFINITO, INFINITO, INFINITO, 0, 8, 6, 2 }, //
	// { INFINITO, 19, INFINITO, 7, 8, 0, 7, 3 }, //
	// { 3, 3, INFINITO, INFINITO, 6, 7, 0, INFINITO }, //
	// { INFINITO, INFINITO, INFINITO, INFINITO, 2, 3, INFINITO, 0 }, //
	// };
	// int[][] matriz = { //
	// { 0, 12, 4, INFINITO, INFINITO, INFINITO, 3, INFINITO }, //
	// { 12, 0, 6, 10, INFINITO, 19, 3, INFINITO }, //
	// { 4, 6, 0, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, //
	// { INFINITO, 10, INFINITO, 0, INFINITO, 7, INFINITO, INFINITO }, //
	// { INFINITO, INFINITO, INFINITO, INFINITO, 0, 8, 6, 2 }, //
	// { INFINITO, 19, INFINITO, 7, 8, 0, 7, 3 }, //
	// { INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, //
	// { INFINITO, INFINITO, INFINITO, INFINITO, 2, 3, INFINITO, 0 }, //
	// };

	CarteiroChines carteiroChines = new CarteiroChines(matriz);
	carteiroChines.executar();
    }

    private CarteiroChines(int[][] matriz) {
	if (matriz != null && matriz.length > 0) {
	    tamanho = matriz.length;
	    if (tamanho == matriz[0].length) {
		this.matriz = matriz;
		criarMatriz();
		if (grafoEhDirigido()) {
		    throw new IllegalArgumentException("O grafo não pode ser dirigido.");
		}
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

    private boolean grafoEhDirigido() {
	Set<Character> vertices = new HashSet<>();
	for (Vertice vertice : grafo) {
	    if (!vertice.arestas.isEmpty()) {
		vertices.add(vertice.nome);
		if (vertices.size() == tamanho) {
		    return false;
		}
	    }
	}
	return true;
    }

    private void executar() {
	custoOriginal = 0;
	custoAdicional = 0;
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
	    throw new IllegalArgumentException("A quantidade de vértices de grau limpar deve ser par.");
	}
    }

    private Set<Vertice> getVerticesGrauImpar() {
	Set<Vertice> verticesGrauImpar = new LinkedHashSet<>();
	for (Vertice vertice : grafo.vertices.values()) {
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
	for (CaminhoDijkstra caminho : permutacaoMenorCusto) {
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
	String resultado = "";
	Grafo grafoTemp = new Grafo(grafo);

	Vertice vProximo = grafoTemp.vertices.values().iterator().next();
	while (!grafoTemp.vertices.isEmpty()) {
	    Aresta aresta = getProximaArestaRemovida(new Grafo(grafoTemp), vProximo);
	    removerAresta(grafoTemp, aresta);
	    if (aresta.virtual) {
		custoAdicional += aresta.custo;
	    } else {
		custoOriginal += aresta.custo;
	    }
	    resultado += aresta.vOrigem.nome + "" + aresta.custo + "" + aresta.vDestino.nome + " ";
	    vProximo = grafoTemp.vertices.get(aresta.vDestino.nome);
	    if (grafoTemp.vertices.get(aresta.vOrigem.nome).arestas.isEmpty()) {
		grafoTemp.vertices.remove(aresta.vOrigem.nome);
	    }
	    if (grafoTemp.vertices.get(aresta.vDestino.nome).arestas.isEmpty()) {
		grafoTemp.vertices.remove(aresta.vDestino.nome);
	    }
	}
	System.out.println("ciclo euleriano " + resultado);
	System.out.println("custo total " + custoOriginal + "+" + custoAdicional + "=" + (custoOriginal + custoAdicional));
    }

    private Aresta getProximaArestaRemovida(Grafo grafo, Vertice vertice) {
	Aresta aresta;
	for (int i = 0; i < grafo.vertices.get(vertice.nome).arestas.size(); i++) {
	    aresta = grafo.vertices.get(vertice.nome).arestas.get(i);
	    removerAresta(grafo, aresta);
	    if (ehConexo(grafo)) {
		return aresta;
	    } else {
		// Deixou o grafo desconexo, devolve a aresta e tenta com a próxima aresta
		grafo.vertices.get(vertice.nome).arestas.add(aresta);
		Aresta arestaInversa = aresta.getArestaDuplicada();
		grafo.vertices.get(arestaInversa.vOrigem.nome).arestas.add(arestaInversa);
	    }
	}
	return null;
	// return getProximaArestaRemovida(grafo, aresta.vDestino);
    }

    private boolean ehConexo(Grafo grafo) {
	return getCiclosDisjuntos(grafo).size() <= 1;
    }

    private Map<Integer, Set<Character>> getCiclosDisjuntos(Grafo grafo) {
	Map<Integer, Set<Character>> conjuntosDisjuntos = new HashMap<>();
	for (Vertice vertice : grafo) {
	    if (!vertice.arestas.isEmpty()) {
		for (Aresta aresta : vertice) {
		    addAresta(conjuntosDisjuntos, aresta);
		}
	    }
	}
	return conjuntosDisjuntos;
    }

    private void addAresta(Map<Integer, Set<Character>> conjuntosDisjuntos, Aresta aresta) {
	if (conjuntosDisjuntos.isEmpty()) {
	    addConjunto(conjuntosDisjuntos, aresta);
	} else {
	    Stream<Entry<Integer, Set<Character>>> filter = conjuntosDisjuntos.entrySet().stream()
		    .filter(c -> c.getValue().contains(aresta.vOrigem.nome) || c.getValue().contains(aresta.vDestino.nome));
	    long count = filter.count();
	    if (count == 2) {
		filter = conjuntosDisjuntos.entrySet().stream().filter(c -> c.getValue().contains(aresta.vOrigem.nome) || c.getValue().contains(aresta.vDestino.nome));
		Iterator<Entry<Integer, Set<Character>>> iterator = filter.iterator();
		Entry<Integer, Set<Character>> e1 = iterator.next();
		Entry<Integer, Set<Character>> e2 = iterator.next();
		e1.getValue().addAll(e2.getValue());
		conjuntosDisjuntos.remove(e2.getKey());
	    } else if (count == 1) {
		filter = conjuntosDisjuntos.entrySet().stream().filter(c -> c.getValue().contains(aresta.vOrigem.nome) || c.getValue().contains(aresta.vDestino.nome));
		Set<Character> c = filter.iterator().next().getValue();
		if (c.contains(aresta.vOrigem.nome)) {
		    c.add(aresta.vDestino.nome);
		} else if (c.contains(aresta.vDestino.nome)) {
		    c.add(aresta.vOrigem.nome);
		}
	    } else {
		addConjunto(conjuntosDisjuntos, aresta);
	    }
	}
    }

    private void addConjunto(Map<Integer, Set<Character>> conjuntosDisjuntos, Aresta aresta) {
	Set<Character> conjunto = new LinkedHashSet<>();
	conjunto.add(aresta.vOrigem.nome);
	conjunto.add(aresta.vDestino.nome);
	conjuntosDisjuntos.put(conjuntosDisjuntos.size() + 1, conjunto);
    }

    private void removerAresta(Grafo grafo, Aresta aresta) {
	if (grafo.vertices.containsKey(aresta.vOrigem.nome)) {
	    grafo.vertices.get(aresta.vOrigem.nome).arestas.removeIf(a -> a.equals(aresta));
	}
	Aresta arestaInversa = aresta.getArestaDuplicada();
	if (grafo.vertices.containsKey(arestaInversa.vOrigem.nome)) {
	    grafo.vertices.get(arestaInversa.vOrigem.nome).arestas.removeIf(a -> a.equals(arestaInversa));
	}
    }

    private static class Grafo implements Iterable<Vertice> {
	private Map<Character, Vertice> vertices = new LinkedHashMap<>();

	public Grafo() {
	}

	/**
	 * Copy constructor
	 */
	public Grafo(Grafo grafo) {
	    for (Vertice vertice : grafo) {
		vertices.put(vertice.nome, new Vertice(vertice));
	    }
	}

	private void addVertice(Vertice vertice) {
	    vertices.put(vertice.nome, vertice);
	}

	private void duplicarAresta(Aresta aresta) {
	    List<Aresta> arestas = vertices.get(aresta.vOrigem.nome).arestas;
	    Aresta arestaDuplicada = arestas.stream().filter(a -> a.equals(aresta)).findFirst().get();
	    arestas.add(new Aresta(arestaDuplicada).setVirtual(true));

	    Aresta outraAresta = aresta.getArestaDuplicada();
	    arestas = vertices.get(outraAresta.vOrigem.nome).arestas;
	    arestaDuplicada = arestas.stream().filter(a -> a.equals(outraAresta)).findFirst().get();
	    arestas.add(new Aresta(arestaDuplicada).setVirtual(true));
	}

	private void addAresta(Aresta aresta) {
	    vertices.get(aresta.vOrigem.nome).arestas.add(aresta);
	}

	@Override
	public Iterator<Vertice> iterator() {
	    return vertices.values().iterator();
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    return vertices.toString();
	}

    }

    private static class ConjuntoDeCaminhos implements Iterable<CaminhoDijkstra> {
	private List<CaminhoDijkstra> conjunto = new LinkedList<>();
	private int custoTotal;

	private void addCaminho(CaminhoDijkstra caminho) {
	    custoTotal += caminho.custoTotal;
	    conjunto.add(caminho);
	}

	@Override
	public Iterator<CaminhoDijkstra> iterator() {
	    return conjunto.iterator();
	}

	/**
	 * Somente para debug
	 */
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
	private boolean virtual = false;

	/**
	 * Copy constructor
	 */
	private Aresta(Aresta aresta) {
	    vOrigem = new Vertice(aresta.vOrigem);
	    vDestino = new Vertice(aresta.vDestino);
	    custo = aresta.custo;
	    virtual = aresta.virtual;
	}

	private Aresta(Vertice vOrigem, Vertice vDestino, int custo) {
	    this.vOrigem = vOrigem;
	    this.vDestino = vDestino;
	    this.custo = custo;
	}

	private Aresta(Vertice vOrigem, Vertice vDestino, int custo, boolean virtual) {
	    this.vOrigem = vOrigem;
	    this.vDestino = vDestino;
	    this.custo = custo;
	    this.virtual = virtual;
	}

	private Aresta getArestaDuplicada() {
	    return new Aresta(vDestino, vOrigem, custo, virtual);
	}

	private Aresta setVirtual(boolean virtual) {
	    this.virtual = virtual;
	    return this;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + custo;
	    result = prime * result + ((vDestino == null) ? 0 : vDestino.hashCode());
	    result = prime * result + ((vOrigem == null) ? 0 : vOrigem.hashCode());
	    result = prime * result + (virtual ? 1231 : 1237);
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
	    if (custo != other.custo)
		return false;
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
	    if (virtual != other.virtual)
		return false;
	    return true;
	}

	/**
	 * Somente para debug
	 */
	@Override
	public String toString() {
	    return virtual ? "virtual " : "" + vOrigem.nome + "->" + vDestino.nome;
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
		int custoVerticeAnterior = Integer.parseInt(dijkstra[0][verticeAnterior.getPosicao()]);
		int custoVerticeAtual = Integer.parseInt(dijkstra[0][vAtual.getPosicao()]);
		int custo = custoVerticeAnterior - custoVerticeAtual;
		Aresta aresta = new Aresta(verticeAnterior, vAtual, custo);
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

    private static class Vertice implements Iterable<Aresta> {
	private char nome;
	private List<Aresta> arestas = new LinkedList<>();

	private Vertice(int posicao) {
	    this.nome = getNome(posicao);
	}

	private Vertice(char nome) {
	    this.nome = nome;
	}

	/**
	 * Copy constructor
	 */
	public Vertice(Vertice vertice) {
	    nome = vertice.nome;
	    for (Aresta aresta : vertice) {
		arestas.add(aresta);
	    }
	}

	private int getPosicao() {
	    return nome - 65;
	}

	private static char getNome(int posicao) {
	    return ((char) (65 + posicao));
	}

	@Override
	public Iterator<Aresta> iterator() {
	    return arestas.iterator();
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
