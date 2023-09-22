//@author Roberto Santana 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Declaração das variáveis de login e senha
public class SistemaLoginCadastro extends JFrame {
    private JTextField campoLogin; 
    private JPasswordField campoSenha;
    
    //Config da interface 
    public SistemaLoginCadastro() {
        setTitle("Sistema de Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Fecha a janela quando clicar em "X"

        JPanel painel = new JPanel(); //Painel para organizar os componentes
        painel.setLayout(new GridLayout(3, 2));

        JLabel labelLogin = new JLabel("Login:");
        JLabel labelSenha = new JLabel("Senha:");

        campoLogin = new JTextField(); // campo de texto para login
        campoSenha = new JPasswordField(); // campo de texto para senha

        JButton botaoEntrar = new JButton("Entrar"); 
        JButton botaoCadastrar = new JButton("Cadastrar Novo Usuário");

        //Ação de clique no botão "Entrar"
        botaoEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String login = campoLogin.getText(); //Recebe o texto inserido no Login
                String senha = new String(campoSenha.getPassword()); //Recebe a senha inserida na senha

                //Chama o método autenticarUsuario para verificar o login e a senha
                if (autenticarUsuario(login, senha)) {
                    JOptionPane.showMessageDialog(null, "Acesso Autorizado");
                } else {
                    JOptionPane.showMessageDialog(null, "Acesso Negado");
                }
            }
        });

        botaoCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Abre um diálogo para inserir os dados do novo usuário
                String nome = JOptionPane.showInputDialog("Nome:");
                String login = JOptionPane.showInputDialog("Login:");
                String senha = JOptionPane.showInputDialog("Senha:");
                String email = JOptionPane.showInputDialog("Email:");

                // Chame o método cadastrarUsuario para cadastrar o novo usuário
                if (ConexaoMySQL.cadastrarUsuario(nome, login, senha, email)) {
                    JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário");
                }
            }
        });
        //Adiciona os componentes ao painel
        painel.add(labelLogin);
        painel.add(campoLogin);
        painel.add(labelSenha);
        painel.add(campoSenha);
        painel.add(botaoEntrar);
        painel.add(botaoCadastrar);

        add(painel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    //Método para autenticar um usuário no sistema
    private boolean autenticarUsuario(String login, String senha) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //Estabelece a conexão com o banco de dados
            connection = DriverManager.getConnection("jdbc:mysql://localhost/mapa", "root", "");
            System.out.println("Conexão com o banco de dados estabelecida.");
            
            //Cria uma consulta para verificar o login e a senha do usuário
            String query = "SELECT id, nome, login, senha, email FROM usuario WHERE login = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, login);

            //Executa a consulta
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                //Se a consulta retornar algum resultado, obtém a senha do bd
                String senhaDoBanco = resultSet.getString("senha");
                //Compara a senha inserida pelo usuário com a senha do bd
                if (senha.equals(senhaDoBanco)) {
                    System.out.println("Login bem-sucedido.");
                    return true;
                }
            }
            
            System.out.println("Acesso Negado: Usuário ou senha incorretos."); //caso o login falhe
            return false;
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao acessar o banco: " + e.getMessage());
            return false;
        } finally {
            try {
                //fecha os recursos do bd
                if (resultSet != null) { 
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }
    //Metodo principal que inicia o swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SistemaLoginCadastro();
            }
        });
    }
}