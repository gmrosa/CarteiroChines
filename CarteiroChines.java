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
import java.util.stream.Collectors;

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
	// XXX Usar infinito quando não houver ligação entre vertices, assim é possível utilizar arestas com custo negativo
	int[][] matriz = { //
		/* A */{ 0, 8, INFINITO, INFINITO, INFINITO, 11, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, // A
		/* B */{ 8, 0, 3, INFINITO, INFINITO, 13, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, // B
		/* C */{ INFINITO, 3, 0, 9, INFINITO, INFINITO, INFINITO, 14, INFINITO, INFINITO, INFINITO, INFINITO }, // C
		/* D */{ INFINITO, INFINITO, 9, 0, 2, INFINITO, INFINITO, INFINITO, 15, INFINITO, INFINITO, INFINITO }, // D
		/* E */{ INFINITO, INFINITO, INFINITO, 2, 0, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, 16 }, // E
		/* F */{ 11, 13, INFINITO, INFINITO, INFINITO, 0, 4, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO }, // F
		/* G */{ INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, 4, 0, 5, INFINITO, 12, INFINITO, INFINITO }, // G
		/* H */{ INFINITO, INFINITO, 14, INFINITO, INFINITO, INFINITO, 5, 0, 1, INFINITO, INFINITO, INFINITO }, // H
		/* I */{ INFINITO, INFINITO, INFINITO, 15, INFINITO, INFINITO, INFINITO, 1, 0, INFINITO, 7, INFINITO }, // I
		/* J */{ INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, 12, INFINITO, INFINITO, 0, 6, INFINITO }, // J
		/* K */{ INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, 7, 6, 0, 10 }, // K
		/* L */{ INFINITO, INFINITO, INFINITO, INFINITO, 16, INFINITO, INFINITO, INFINITO, INFINITO, INFINITO, 10, 0 }, // L
	};
	CarteiroChines carteiroChines = new CarteiroChines(matriz);
	carteiroChines.executar();
    }

    private CarteiroChines(int[][] matriz) {
	if (matriz != null && matriz.length > 0) {
	    tamanho = matriz.length;
	    if (tamanho == matriz[0].length) {
		this.matriz = matriz;
		criarGrafo();
		if (temVerticesIsolados()) {
		    throw new IllegalArgumentException("O grafo não pode ser dirigido ou ter vertices isolados.");
		} else if (!ehConexo(grafo)) {
		    throw new IllegalArgumentException("O grafo não é conexo.");
		}
	    } else {
		throw new IllegalArgumentException("A matriz deve ser uma matriz quadrada.");
	    }
	} else {
	    throw new IllegalArgumentException("Matriz inválida.");
	}
    }

    private void criarGrafo() {
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

    private boolean temVerticesIsolados() {
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
	ConjuntoDeCaminhosDijkstra permutacaoMenorCusto = getPermutacaoMenorCusto();
	for (CaminhoDijkstra caminho : permutacaoMenorCusto) {
	    for (Aresta aresta : caminho.arestas) {
		grafo.duplicarAresta(aresta);
	    }
	}
    }

    private ConjuntoDeCaminhosDijkstra getPermutacaoMenorCusto() {
	int custo = INFINITO;
	ConjuntoDeCaminhosDijkstra resultado = null;
	for (ConjuntoDeCaminhosDijkstra caminho : getPermutacaoDosCaminhos()) {
	    if (caminho.custoTotal < custo) {
		custo = caminho.custoTotal;
		resultado = caminho;
	    }
	}
	return resultado;
    }

    private List<ConjuntoDeCaminhosDijkstra> getPermutacaoDosCaminhos() {
	Map<Aresta, CaminhoDijkstra> dijkstrasTemp = new LinkedHashMap<>(dijkstras);
	List<ConjuntoDeCaminhosDijkstra> caminhos = new ArrayList<>();
	Set<Vertice> verticesGrauImparTemp = new LinkedHashSet<>();

	while (!dijkstrasTemp.isEmpty()) {
	    verticesGrauImparTemp.addAll(verticesGrauImpar);
	    ConjuntoDeCaminhosDijkstra conjuntoDeCaminhos = new ConjuntoDeCaminhosDijkstra();
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
	// Como o grafo é dirigido nunca vai cair aqui
	throw new RuntimeException("Erro ao eulerizar grafo.");
    }

    private boolean ehConexo(Grafo grafo) {
	return new CiclosDisjuntos(grafo).get().size() <= 1;
    }

    /**
     * Remove o par de arestas equivalentes, ex: A->B exclui também B->A. Caso tenha arestas paralelas não haverá problema, pois quando é
     * criado arestas paralelas ela é virtual e elas são tratadas com diferenciação pelo equals. Se removeu aresta normal de uma ponta,
     * exclui na outra, removeu aresta virtual numa ponta, exclui a virtual na outra.
     */
    private void removerAresta(Grafo grafo, Aresta aresta) {
	// A->B
	if (grafo.vertices.containsKey(aresta.vOrigem.nome)) {
	    for (int i = 0; i < grafo.vertices.get(aresta.vOrigem.nome).arestas.size(); i++) {
		Aresta a = grafo.vertices.get(aresta.vOrigem.nome).arestas.get(i);
		if (a.equals(aresta)) {
		    // Remove somente o primeiro
		    grafo.vertices.get(aresta.vOrigem.nome).arestas.remove(i);
		    break;
		}
	    }
	}
	// B->A
	Aresta arestaInversa = aresta.getArestaDuplicada();
	if (grafo.vertices.containsKey(arestaInversa.vOrigem.nome)) {
	    for (int i = 0; i < grafo.vertices.get(arestaInversa.vOrigem.nome).arestas.size(); i++) {
		Aresta a = grafo.vertices.get(arestaInversa.vOrigem.nome).arestas.get(i);
		if (a.equals(arestaInversa)) {
		    // Remove somente o primeiro
		    grafo.vertices.get(arestaInversa.vOrigem.nome).arestas.remove(i);
		    break;
		}
	    }
	}
    }

    private class CiclosDisjuntos {
	private Grafo grafo;

	public CiclosDisjuntos(Grafo grafo) {
	    this.grafo = grafo;
	}

	private Map<Integer, Set<Character>> get() {
	    Map<Integer, Set<Character>> conjuntosDisjuntos = new HashMap<>();
	    int i = 0;
	    for (Vertice vertice : grafo) {
		if (!vertice.arestas.isEmpty()) {
		    for (Aresta aresta : vertice) {
			addAresta(conjuntosDisjuntos, aresta);
		    }
		} else {
		    // O vértice de origem não pode estar isolado, senão o grafo fica desconexo
		    if (i == 0) {
			// Vértice isolado
			Set<Character> conjunto = new LinkedHashSet<>();
			conjunto.add(vertice.nome);
			conjuntosDisjuntos.put(conjuntosDisjuntos.size() + 1, conjunto);
		    }
		}
		i++;
	    }
	    return conjuntosDisjuntos;
	}

	private void addAresta(Map<Integer, Set<Character>> conjuntosDisjuntos, Aresta aresta) {
	    if (conjuntosDisjuntos.isEmpty()) {
		addConjunto(conjuntosDisjuntos, aresta);
	    } else {
		Map<Integer, Set<Character>> conjuntos = conjuntosDisjuntos.entrySet().stream()
			.filter(c -> c.getValue().contains(aresta.vOrigem.nome) || c.getValue().contains(aresta.vDestino.nome))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		if (conjuntos.size() == 2) {
		    Iterator<Entry<Integer, Set<Character>>> iterator = conjuntos.entrySet().iterator();
		    Entry<Integer, Set<Character>> e1 = iterator.next();
		    Entry<Integer, Set<Character>> e2 = iterator.next();
		    e1.getValue().addAll(e2.getValue());
		    conjuntosDisjuntos.remove(e2.getKey());
		} else if (conjuntos.size() == 1) {
		    Set<Character> c = conjuntos.values().iterator().next();
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

    }

    private static class Grafo implements Iterable<Vertice> {
	private Map<Character, Vertice> vertices = new LinkedHashMap<>();

	/**
	 * Copy constructor
	 */
	public Grafo(Grafo grafo) {
	    for (Vertice vertice : grafo) {
		vertices.put(vertice.nome, new Vertice(vertice));
	    }
	}

	public Grafo() {
	}

	private void addVertice(Vertice vertice) {
	    vertices.put(vertice.nome, vertice);
	}

	/**
	 * Gera arestas virtuais Como o grafo é dirigido tem que criar dois pares no conjunto, porém se tratam da mesma aresta. Isso auxilia
	 * a varredura do ciclo euleriano.
	 */
	private void duplicarAresta(Aresta aresta) {
	    // A->B
	    List<Aresta> arestas = vertices.get(aresta.vOrigem.nome).arestas;
	    Aresta arestaDuplicada = arestas.stream().filter(a -> a.equals(aresta)).findFirst().get();
	    arestas.add(new Aresta(arestaDuplicada).setVirtual(true));
	    // B->A
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

    private static class ConjuntoDeCaminhosDijkstra implements Iterable<CaminhoDijkstra> {
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

	/**
	 * Gera uma cópia inversa da aresta ex: A->B gera B->A. Isso auxilia a varredura do ciclo euleriano.
	 */
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

    private static class Vertice implements Iterable<Aresta> {
	private char nome;
	private List<Aresta> arestas = new LinkedList<>();

	/**
	 * Copy constructor
	 */
	public Vertice(Vertice vertice) {
	    nome = vertice.nome;
	    for (Aresta aresta : vertice) {
		arestas.add(aresta);
	    }
	}

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
	    return "[" + nome + " {" + arestas + "}]";
	}

    }

}
