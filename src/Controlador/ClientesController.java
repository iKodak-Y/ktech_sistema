package Controlador;

import Modelo.Cliente;
import Modelo.ClienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClientesController implements Initializable {

    @FXML
    private TextField txt_buscar_cliente;
    @FXML
    private Button btn_buscar;
    @FXML
    private Button btn_limpiar;
    @FXML
    private TextField txt_nombres;
    @FXML
    private TextField txt_apellidos;
    @FXML
    private TextField txt_cedula;
    @FXML
    private ComboBox<String> cmb_tipo_documento;
    @FXML
    private TextField txt_direccion;
    @FXML
    private TextField txt_telefono;
    @FXML
    private TextField txt_email;
    @FXML
    private TextField txt_sexo;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_eliminar;
    @FXML
    private Button btn_cerrar;
    @FXML
    private TableView<Cliente> tbl_clientes;
    @FXML
    private TableColumn<Cliente, String> col_nombres;
    @FXML
    private TableColumn<Cliente, String> col_apellidos;
    @FXML
    private TableColumn<Cliente, String> col_cedula;
    @FXML
    private TableColumn<Cliente, String> col_direccion;
    @FXML
    private TableColumn<Cliente, String> col_telefono;
    @FXML
    private TableColumn<Cliente, String> col_email;
    @FXML
    private TableColumn<Cliente, String> col_sexo;

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado; // Variable para almacenar el cliente seleccionado

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        col_nombres.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_apellidos.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        col_cedula.setCellValueFactory(new PropertyValueFactory<>("cedulaRUC"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_sexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        cargarClientes();
        tbl_clientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarCliente(newSelection);
                clienteSeleccionado = newSelection; // Almacenar el cliente seleccionado
            }
        });

        // Inicializar los items del ComboBox
        cmb_tipo_documento.setItems(FXCollections.observableArrayList("Cedula", "RUC"));
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        listaClientes = FXCollections.observableArrayList(clientes);
        tbl_clientes.setItems(listaClientes);
    }

    private void mostrarCliente(Cliente cliente) {
        if (cliente != null) {
            txt_nombres.setText(cliente.getNombre());
            txt_apellidos.setText(cliente.getApellido());
            txt_cedula.setText(cliente.getCedulaRUC());
            txt_direccion.setText(cliente.getDireccion());
            txt_telefono.setText(cliente.getTelefono());
            txt_email.setText(cliente.getEmail());
            txt_sexo.setText(String.valueOf(cliente.getSexo()));
        }
    }

    private void limpiarCampos() {
        txt_nombres.clear();
        txt_apellidos.clear();
        txt_cedula.clear();
        txt_direccion.clear();
        txt_telefono.clear();
        txt_email.clear();
        txt_sexo.clear();
        cmb_tipo_documento.getSelectionModel().clearSelection();
        clienteSeleccionado = null; // Limpiar el cliente seleccionado
    }

    @FXML
    private void acc_guardar(ActionEvent event) {
        String nombre = txt_nombres.getText();
        String apellido = txt_apellidos.getText();
        String cedula = txt_cedula.getText();
        String tipoDocumento = cmb_tipo_documento.getValue();
        String direccion = txt_direccion.getText();
        String telefono = txt_telefono.getText();
        String email = txt_email.getText();
        char sexo = txt_sexo.getText().charAt(0);
        char estado = 'A'; // Por defecto, el estado es 'A' (activo)

        if (tipoDocumento == null) {
            mostrarAlerta("Documento inválido", "Por favor, seleccione un tipo de documento.");
            return;
        }

        if (!validarDocumento(cedula, tipoDocumento)) {
            mostrarAlerta("Documento inválido", "Por favor, ingrese un número de " + tipoDocumento + " válido.");
            return;
        }

        Cliente clienteExistente = clienteDAO.buscarPorCedulaONombre(cedula);
        if (clienteExistente != null && (clienteSeleccionado == null || clienteExistente.getIdCliente() != clienteSeleccionado.getIdCliente())) {
            mostrarAlerta("Documento duplicado", "El número de " + tipoDocumento + " ya está registrado.");
            return;
        }

        boolean exito;
        if (clienteSeleccionado == null) {
            // Insertar nuevo cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setCedulaRUC(cedula);
            cliente.setDireccion(direccion);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setSexo(sexo);
            cliente.setEstado(estado);
            exito = clienteDAO.guardar(cliente);
        } else {
            // Actualizar cliente existente
            clienteSeleccionado.setNombre(nombre);
            clienteSeleccionado.setApellido(apellido);
            clienteSeleccionado.setCedulaRUC(cedula);
            clienteSeleccionado.setDireccion(direccion);
            clienteSeleccionado.setTelefono(telefono);
            clienteSeleccionado.setEmail(email);
            clienteSeleccionado.setSexo(sexo);
            clienteSeleccionado.setEstado(estado);
            exito = clienteDAO.actualizar(clienteSeleccionado);
        }

        if (exito) {
            mostrarAlerta("Éxito", "Los datos del cliente han sido guardados correctamente.");
        } else {
            mostrarAlerta("Error", "Ocurrió un error al guardar los datos del cliente.");
        }

        cargarClientes();
        limpiarCampos();
    }

    @FXML
    private void acc_eliminar(ActionEvent event) {
        Cliente clienteSeleccionado = tbl_clientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            boolean exito = clienteDAO.eliminar(clienteSeleccionado.getIdCliente());
            if (exito) {
                mostrarAlerta("Éxito", "El cliente ha sido eliminado correctamente.");
            } else {
                mostrarAlerta("Error", "Ocurrió un error al eliminar el cliente.");
            }
            cargarClientes();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "Debe seleccionar un cliente para eliminar.");
        }
    }

    @FXML
    private void acc_buscar(ActionEvent event) {
        String termino = txt_buscar_cliente.getText();
        if (termino.isEmpty()) {
            // Si el campo de búsqueda está vacío, cargar todos los clientes
            cargarClientes();
        } else {
            Cliente cliente = clienteDAO.buscarPorCedulaONombre(termino);
            if (cliente != null) {
                listaClientes = FXCollections.observableArrayList(cliente);
                tbl_clientes.setItems(listaClientes);
            } else {
                tbl_clientes.getItems().clear();
                mostrarAlerta("No encontrado", "No se encontró ningún cliente con el término ingresado.");
            }
        }
    }

    @FXML
    private void acc_limpiar(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void acc_cerrar(ActionEvent event) {
        try {
            // abrir formulario
            String formulario = "/Vista/Principal.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formulario));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.setResizable(false);
            stage.show();
            //cerrar formulario
            Stage myStage = (Stage) this.btn_cerrar.getScene().getWindow();
            myStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validarDocumento(String documento, String tipoDocumento) {
        if (tipoDocumento.equals("Cedula")) {
            return validarCedula(documento);
        } else if (tipoDocumento.equals("RUC")) {
            return validarRUC(documento);
        }
        return false;
    }

    private boolean validarCedula(String cedula) {
        if (cedula.length() != 10) {
            return false;
        }
        int prov = Integer.parseInt(cedula.substring(0, 2));
        if (prov < 1 || prov > 24) {
            return false;
        }
        int[] coef = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < coef.length; i++) {
            int digito = Integer.parseInt(String.valueOf(cedula.charAt(i))) * coef[i];
            suma += digito > 9 ? digito - 9 : digito;
        }
        int verificador = (10 - suma % 10) % 10;
        return verificador == Integer.parseInt(String.valueOf(cedula.charAt(9)));
    }

    private boolean validarRUC(String ruc) {
        if (ruc.length() != 13 || !ruc.endsWith("001")) {
            return false;
        }
        String cedula = ruc.substring(0, 10);
        return validarCedula(cedula);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}