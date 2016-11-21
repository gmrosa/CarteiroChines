import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme Murilo da Rosa & Plamedi Luzolo Lusembo
 */
public class CarteiroChines {
	public static int INFINITO = Integer.MAX_VALUE;
	private int tamanho;
	private Map<Aresta, CaminhoDijkstra> dijkstras;
	private Map<Character, List<Aresta>> grafo = new LinkedHashMap<>();
	private int[][] matriz;

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

		CarteiroChines carteiroChines = new CarteiroChines(matriz);
		carteiroChines.executar();
		System.out.println();
	}

	CarteiroChines(int[][] matriz) {
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
		eulerizarGrafo();
	}

	private void executarDijkstraParaVerticesDeGrauImpar() {
		dijkstras = new LinkedHashMap<>();
		List<Character> verticesGrauImpar = new ArrayList<>();
		for (Map.Entry<Character, List<Aresta>> entry : grafo.entrySet()) {
			int grau = entry.getValue().size();
			if (temGrauImpar(grau)) {
				char vertice = entry.getKey();
				verticesGrauImpar.add(vertice);
			}
		}

		for (Character verticeOrigem : verticesGrauImpar) {
			List<Character> pilhaVertices = new ArrayList<>();
			pilhaVertices.addAll(verticesGrauImpar);
			pilhaVertices.remove(verticeOrigem);
			Dijkstra dijkstra = new Dijkstra(tamanho, matriz);
			dijkstra.executar(verticeOrigem);
			for (Character verticeDestino : pilhaVertices) {
				CaminhoDijkstra caminhoDijkstra = dijkstra.getCaminhoMinimo(verticeDestino);
				int custo = caminhoDijkstra.custoTotal;
				Aresta aresta = new Aresta(verticeOrigem, verticeDestino, custo);
				dijkstras.put(aresta, caminhoDijkstra);
			}
		}

	}

	private boolean temGrauImpar(int grau) {
		return grau % 2 != 0;
	}

	private void eulerizarGrafo() {
		// for (Map.Entry<Character, Dijkstra> entry : dijkstras.entrySet()) {
		// }
	}

	static class CaminhoDijkstra {
		private List<Aresta> caminho = new ArrayList<>();
		private int custoTotal;

		public CaminhoDijkstra(int custoTotal) {
			this.custoTotal = custoTotal;
		}

		public void addAresta(Aresta aresta) {
			caminho.add(aresta);
		}

		public int getCustoTotal() {
			return custoTotal;
		}

	}

	static class Aresta {
		private char verticeOrigem;
		private char verticeDestino;
		private int custo;

		public Aresta(char verticeOrigem, char verticeDestino, int custo) {
			this.verticeOrigem = verticeOrigem;
			this.verticeDestino = verticeDestino;
			this.custo = custo;
		}

		public Aresta(char verticeOrigem, char verticeDestino) {
			this.verticeOrigem = verticeOrigem;
			this.verticeDestino = verticeDestino;
		}

		public Aresta(char verticeDestino, int custo) {
			this.verticeDestino = verticeDestino;
			this.custo = custo;
		}

		public char getVerticeOrigem() {
			return verticeOrigem;
		}

		public char getVerticeDestino() {
			return verticeDestino;
		}

		public int getCusto() {
			return custo;
		}

		@Override
		public String toString() {
			return "[verticeDestino=" + verticeDestino + ", custo=" + custo + "]";
		}

	}

	static class Dijkstra {

		private int tamanho;
		private int posicaoOrigem;
		private int[][] matriz;
		private String dijkstra[][];

		Dijkstra(int tamanho, int[][] matriz) {
			this.tamanho = tamanho;
			this.matriz = matriz;
			this.dijkstra = new String[3][tamanho];
		}

		String[][] executar(char verticeOrigem) {
			posicaoOrigem = Vertice.getPosicao(verticeOrigem);
			inicializarDijkstra();
			return gerarDijkstra();
		}

		private void inicializarDijkstra() {
			for (int coluna = 0; coluna < tamanho; coluna++) {
				if (posicaoOrigem == coluna) {
					dijkstra[0][coluna] = "0";
					dijkstra[1][coluna] = "nil";
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
			int verticeMenorCusto = -1;
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

		CaminhoDijkstra getCaminhoMinimo(char verticeDestino) {
			int posicaoAtual = posicaoOrigem;
			int posicaoDestino = Vertice.getPosicao(verticeDestino);
			int custoTotal = Integer.parseInt(dijkstra[0][posicaoDestino]);

			CaminhoDijkstra caminhoDijkstra = new CaminhoDijkstra(custoTotal);

			while (!dijkstra[1][posicaoAtual].equals(String.valueOf(verticeDestino))) {
				char verticeOrigem = Vertice.getNome(posicaoAtual);
				posicaoAtual = Vertice.getPosicao(dijkstra[1][Vertice.getPosicao(verticeOrigem)].charAt(0));
				char verticeDestino2 = Vertice.getNome(posicaoAtual);
				Aresta aresta = new Aresta(verticeOrigem, verticeDestino2);
				caminhoDijkstra.addAresta(aresta);
			}
			return caminhoDijkstra;
		}

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

	static class Vertice {

		static char getNome(int posicao) {
			return ((char) (65 + posicao));
		}

		static int getPosicao(char nomeVertice) {
			return nomeVertice - 65;
		}

	}

}
