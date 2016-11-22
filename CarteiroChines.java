import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme Murilo da Rosa & Plamedi Luzolo Lusembo
 */
public class CarteiroChines {
	private static int INFINITO = Integer.MAX_VALUE;
	private int tamanho;
	private List<Character> verticesGrauImpar = new LinkedList<>();
	private Map<Aresta, CaminhoDijkstra> dijkstras = new LinkedHashMap<>();
	private Map<Character, List<Aresta>> grafo = new LinkedHashMap<>();
	private int[][] matriz;

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
		carteiroChines.executar();
		System.out.println();
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
			char verticeOrigem = Vertice.getNome(i);
			this.grafo.put(verticeOrigem, new ArrayList<>());

			for (int j = 0; j < tamanho; j++) {
				char verticeDestino = Vertice.getNome(j);
				int custo = matriz[i][j];
				if (i != j && custo < INFINITO) {
					Aresta aresta = new Aresta(verticeDestino, custo);
					this.grafo.get(verticeOrigem).add(aresta);
				}
			}
		}
	}

	private void executar() {
		executarDijkstraParaVerticesDeGrauImpar();
		duplicarCaminhosDeMenorCusto();
		eulerizarGrafo();
	}

	private void executarDijkstraParaVerticesDeGrauImpar() {
		List<Character> verticesGrauImpar = getVerticesGrauImpar();
		if (ehPar(verticesGrauImpar.size())) {
			while (!verticesGrauImpar.isEmpty()) {
				Character verticeOrigem = verticesGrauImpar.remove(0);
				if (!verticesGrauImpar.isEmpty()) {
					Dijkstra dijkstra = new Dijkstra(tamanho, matriz);
					dijkstra.executar(verticeOrigem);
					for (Character verticeDestino : verticesGrauImpar) {
						CaminhoDijkstra caminhoDijkstra = dijkstra.getCaminhoMinimo(verticeDestino);
						int custo = caminhoDijkstra.custoTotal;
						Aresta aresta = new Aresta(verticeOrigem, verticeDestino, custo);
						dijkstras.put(aresta, caminhoDijkstra);
					}
				}
			}
		} else {
			throw new IllegalArgumentException("A quantidade de vértices de grau impar deve ser par.");
		}
	}

	private List<Character> getVerticesGrauImpar() {
		this.verticesGrauImpar = new ArrayList<>();
		List<Character> verticesGrauImpar = new ArrayList<>();
		for (Map.Entry<Character, List<Aresta>> entry : grafo.entrySet()) {
			int grau = entry.getValue().size();
			boolean temGrauImpar = !ehPar(grau);
			if (temGrauImpar) {
				char vertice = entry.getKey();
				verticesGrauImpar.add(vertice);
			}
		}
		return verticesGrauImpar;
	}

	private boolean ehPar(int grau) {
		return grau % 2 == 0;
	}

	private void duplicarCaminhosDeMenorCusto() {
		Map<Aresta, CaminhoDijkstra> dijkstrasTemp = new LinkedHashMap<>(dijkstras);
		List<ConjuntoDeCaminhos> caminhos = new ArrayList<>();

		int quantidadeMaximaCombinacoes = verticesGrauImpar.size() - 1;
		List<Character> verticesGrauImparTemp = new LinkedList<>();

		while (caminhos.size() < quantidadeMaximaCombinacoes) {
			verticesGrauImparTemp.addAll(verticesGrauImparTemp);
			Character vertice = verticesGrauImparTemp.remove(0);
			ConjuntoDeCaminhos conjuntoDeCaminhos = new ConjuntoDeCaminhos();
			while (conjuntoDeCaminhos.conjunto.size() < verticesGrauImparTemp.size() / 2) {
				for (Map.Entry<Aresta, CaminhoDijkstra> entry : dijkstrasTemp.entrySet()) {
					Aresta aresta = entry.getKey();
					if (aresta.verticeOrigem == vertice) {
					
					}
				}
			}

		}

	}

	private void eulerizarGrafo() {
		// for (Map.Entry<Character, Dijkstra> entry : dijkstras.entrySet()) {
		// }
	}

	private static class ConjuntoDeCaminhos {
		private List<CaminhoDijkstra> conjunto = new LinkedList<>();
		private int custoTotal;

		private void addCaminho(CaminhoDijkstra caminho) {
			custoTotal += caminho.custoTotal;
			conjunto.add(caminho);
		}

	}

	private static class CaminhoDijkstra {
		private List<Aresta> caminho = new ArrayList<>();
		private int custoTotal;

		private CaminhoDijkstra(int custoTotal) {
			this.custoTotal = custoTotal;
		}

		private void addAresta(Aresta aresta) {
			caminho.add(aresta);
		}

		@Override
		public String toString() {
			return "CaminhoDijkstra [caminho=" + caminho + ", custoTotal=" + custoTotal + "]";
		}

	}

	private static class Aresta {
		private char verticeOrigem;
		private char verticeDestino;
		private int custo;

		private Aresta(char verticeOrigem, char verticeDestino, int custo) {
			this.verticeOrigem = verticeOrigem;
			this.verticeDestino = verticeDestino;
			this.custo = custo;
		}

		private Aresta(char verticeOrigem, char verticeDestino) {
			this.verticeOrigem = verticeOrigem;
			this.verticeDestino = verticeDestino;
		}

		private Aresta(char verticeDestino, int custo) {
			this.verticeDestino = verticeDestino;
			this.custo = custo;
		}

		/**
		 * Somente para debug
		 */
		@Override
		public String toString() {
			if (verticeOrigem > 0) {
				if (custo > 0) {
					return "[verticeOrigem=" + verticeOrigem + ", verticeDestino=" + verticeDestino + ", custo=" + custo + "]";
				}
				return "[verticeOrigem=" + verticeOrigem + ", verticeDestino=" + verticeDestino + "]";
			}
			return "[verticeDestino=" + verticeDestino + ", custo=" + custo + "]";
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

		private String[][] executar(char verticeOrigem) {
			posicaoOrigem = Vertice.getPosicao(verticeOrigem);
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

		private CaminhoDijkstra getCaminhoMinimo(char verticeDestino) {
			int posicaoAtual = Vertice.getPosicao(verticeDestino);
			int custoTotal = Integer.parseInt(dijkstra[0][posicaoAtual]);

			CaminhoDijkstra caminhoDijkstra = new CaminhoDijkstra(custoTotal);

			while (dijkstra[1][posicaoAtual] != null) {
				char verticeTemp = Vertice.getNome(posicaoAtual);
				posicaoAtual = Vertice.getPosicao(dijkstra[1][Vertice.getPosicao(verticeTemp)].charAt(0));
				Aresta aresta = new Aresta(verticeTemp, Vertice.getNome(posicaoAtual));
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

		static char getNome(int posicao) {
			return ((char) (65 + posicao));
		}

		static int getPosicao(char nomeVertice) {
			return nomeVertice - 65;
		}

	}

}
