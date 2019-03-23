package zct.sistemas.leko.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zct.sistemas.leko.dao.DadosHeaderDAO;
import zct.sistemas.leko.model.DadosHeader;
import zct.sistemas.leko.shared.DadosHeaderShared;
import zct.sistemas.leko.util.AlertUtil;
import zct.sistemas.leko.util.NotificationsUtil;

public class DadosHeaderController implements Initializable {

	@FXML
	private TextField txtNomeEmpresa;
	@FXML
	private TextField txtEndereco;
	@FXML
	private TextField txtCidade;
	@FXML
	private TextField txtEstado;
	@FXML
	private TextField txtTelefone;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtCep;
	@FXML
	private ImageView imgLogo;
	@FXML
	private Button btLogo;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	private DadosHeaderDAO dadosHeaderDAO = new DadosHeaderDAO();
	private DadosHeader dados;
	private String logoPath;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btSave.setGraphic(new ImageView(new Image(getClass().getResource("/icons/save.png").toExternalForm())));
		btCancel.setGraphic(new ImageView(new Image(getClass().getResource("/icons/cancel.png").toExternalForm())));
		btLogo.setGraphic(new ImageView(new Image(getClass().getResource("/icons/pencil.png").toExternalForm())));

		dados = DadosHeaderShared.getDadosHeader();
		txtNomeEmpresa.setText(dados.getNomeEmpresa());
		txtEndereco.setText(dados.getEndereco());
		txtCidade.setText(dados.getCidade());
		txtEstado.setText(dados.getEstado());
		txtTelefone.setText(dados.getTelefone());
		txtEmail.setText(dados.getEmail());
		txtCep.setText(dados.getCep());
		logoPath = dados.getLogo();
		if (dados.getLogo() != null)
			imgLogo.setImage(new Image(getLogoPath(dados.getLogo())));

	}

	@FXML
	void save(ActionEvent event) {
		if (validateFields() == false) {
			return;
		}
		dados.setNomeEmpresa(txtNomeEmpresa.getText());
		dados.setEndereco(txtEndereco.getText());
		dados.setCidade(txtCidade.getText());
		dados.setEstado(txtEstado.getText());
		dados.setTelefone(txtTelefone.getText());
		dados.setEmail(txtEmail.getText());
		dados.setCep(txtCep.getText());
		dados.setLogo(logoPath);
		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				dadosHeaderDAO.updateItem(dados);
				return null;
			}
		};
		saveTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				Stage stage = (Stage) txtCep.getScene().getWindow();
				stage.close();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						NotificationsUtil.makeInfo("Edição de dados", "Dados gravados com sucesso.");
					}
				});
			}
		});
		saveTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				AlertUtil.makeError("Erro", "Ocorreu um erro ao tentar gravar os dados.");
				Stage stage = (Stage) txtCep.getScene().getWindow();
				stage.close();
			}
		});
		new Thread(saveTask).run();
	}

	@FXML
	private void cancel() {
		Stage stage = (Stage) txtCep.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void editLogo() throws Exception {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			try {
				String imageUrl = file.toURI().toURL().toExternalForm();
				logoPath = file.getCanonicalPath();
				Image image = new Image(imageUrl);
				imgLogo.setImage(image);
			} catch (MalformedURLException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	private boolean validateFields() {
		if ((txtNomeEmpresa.getText() == null || "".equals(txtNomeEmpresa.getText().trim()))) {
			AlertUtil.makeWarning("Atenção", "Informe o nome da empresa.");
			txtNomeEmpresa.requestFocus();
			return false;
		}
		return true;
	}

	private String getLogoPath(String url) {
		String baseDir = "";
		try {
			baseDir = new File(url).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		baseDir = baseDir.replace('\\', '/');
		return "file:///" + baseDir;
	}

}
