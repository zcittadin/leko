package zct.sistemas.leko.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import zct.sistemas.leko.model.DadosHeader;
import zct.sistemas.leko.model.Orcamento;

public class OrcamentoReport {

	public static int generatePdfReport(String filePath, Orcamento orcamento, DadosHeader dadosHeader) {

		JasperReportBuilder builder = DynamicReports.report();
		ReportTemplateBuilder reportTemplate = DynamicReports.template();

		TextColumnBuilder<String> quantidadeColumn = col.column("Quantidade", "quantidade", type.stringType());
		TextColumnBuilder<String> unidadeColumn = col.column("Unidade", "unidade", type.stringType());
		TextColumnBuilder<String> descricaoColumn = col.column("Descrição", "descricao", type.stringType())
				.setMinWidth(300);
		TextColumnBuilder<String> valorUnitarioColumn = col.column("Valor unitário", "valorUnitario",
				type.stringType());
		TextColumnBuilder<String> subtotalColumn = col.column("Sub-total", "subtotal", type.stringType());

		StyleBuilder mainHeaderStyle = stl.style().setFontSize(14)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		StyleBuilder titleStyle = stl.style().setFontSize(14).bold();
		StyleBuilder subTitleStyle = stl.style().setFontSize(10)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		StyleBuilder columnHeaderStyle = stl.style().setBorder(stl.pen1Point()).setPadding(5)
				.setForegroundColor(Color.WHITE).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
				.setBackgroundColor(Color.decode("#008000")).bold();
		StyleBuilder columnStyle = stl.style().setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setFontSize(8);
		StyleBuilder maoDeObraStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
				.setFontSize(12);
		StyleBuilder totalStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(24);

		reportTemplate.setLocale(Locale.getDefault()).setColumnStyle(columnStyle).setColumnTitleStyle(columnHeaderStyle)
				.highlightDetailEvenRows().crosstabHighlightEvenRows();

		builder.setTemplate(reportTemplate);

		String path = dadosHeader.getLogo().replace("\\", "\\\\");

		String cidade = "";
		String estado = "";
		String cidadeEstado = "";
		if (dadosHeader.getCidade() != null && !"".equals(dadosHeader.getCidade())) {
			cidade = cidade.concat(dadosHeader.getCidade());
		}
		if (dadosHeader.getEstado() != null && !"".equals(dadosHeader.getEstado())) {
			estado = estado.concat(dadosHeader.getEstado());
		}
		if (!"".equals(cidade) && !"".equals(estado))
			cidadeEstado = cidadeEstado.concat(cidade).concat(" - ").concat(estado);
		if (!"".equals(cidade) && "".equals(estado))
			cidadeEstado = cidade;
		if ("".equals(cidade) && !"".equals(estado))
			cidadeEstado = estado;

		builder.title(cmp.horizontalList()
				.add(cmp.horizontalList(cmp.image(path).setDimension(45, 70), cmp.horizontalGap(10), cmp.verticalList(
						cmp.text(dadosHeader.getNomeEmpresa()).setStyle(mainHeaderStyle)
								.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
						(dadosHeader.getEndereco() == null || "".equals(dadosHeader.getEndereco())) ? cmp.gap(0, 0)
								: cmp.text(dadosHeader.getEndereco()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
						("".equals(cidadeEstado)) ? cmp.gap(0, 0)
								: cmp.text(cidadeEstado).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
						(dadosHeader.getCep() == null || "".equals(dadosHeader.getCep())) ? cmp.gap(0, 0)
								: cmp.text("CEP: " + dadosHeader.getCep()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
						(dadosHeader.getTelefone() == null || "".equals(dadosHeader.getTelefone())) ? cmp.gap(0, 0)
								: cmp.text("FONE: " + dadosHeader.getTelefone()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
						(dadosHeader.getEmail() == null || "".equals(dadosHeader.getEmail())) ? cmp.gap(0, 0)
								: cmp.text("E-MAIL: " + dadosHeader.getEmail()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)),
						cmp.horizontalGap(10))));

		builder.title(cmp.horizontalList(cmp.verticalList(cmp.verticalGap(10), cmp.line(), cmp.verticalGap(10))));

		builder.title(cmp.horizontalList(
				cmp.text("ORÇAMENTO").setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setStyle(titleStyle)));

		builder.title(cmp.horizontalList(cmp.text("DESCRIÇÃO DOS SERVIÇOS: " + orcamento.getDescricaoServicos())));

		builder.title(cmp.horizontalList(cmp.verticalList(cmp.verticalGap(10), cmp.line(), cmp.verticalGap(10))));

		builder.setDataSource(orcamento.getItens()).columns(quantidadeColumn, unidadeColumn, descricaoColumn,
				valorUnitarioColumn, subtotalColumn);

		builder.addSummary(
				cmp.verticalList(
						cmp.horizontalList(cmp.verticalList(cmp.verticalGap(10), cmp.line(), cmp.verticalGap(10)))),
				cmp.text("Mão-de-obra:  " + orcamento.getMaoDeObra()).setStyle(maoDeObraStyle));
		builder.addSummary(
				cmp.verticalList(
						cmp.horizontalList(cmp.verticalList(cmp.verticalGap(10), cmp.line(), cmp.verticalGap(10)))),
				cmp.text("Total:  " + orcamento.getTotal()).setStyle(totalStyle));

		builder.addPageFooter(cmp.verticalGap(5), (cmp.horizontalList(cmp
				.text("Data de emissão: "
						+ DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss").format(LocalDateTime.now()))
				.setStyle(stl.style().setFontSize(6)).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))));

		try {
			builder.toPdf(export.pdfExporter(filePath));
			return 1;
		} catch (DRException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
