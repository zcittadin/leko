package zct.sistemas.leko.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
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
		TextColumnBuilder<String> descricaoColumn = col.column("Descrição", "descricao", type.stringType());
		TextColumnBuilder<String> valorUnitarioColumn = col.column("Valor unitário", "valorUnitario",
				type.stringType());
		TextColumnBuilder<String> subtotalColumn = col.column("Sub-total", "subtotal", type.stringType());

		StyleBuilder mainHeaderStyle = stl.style().setFontSize(14)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		StyleBuilder titleStyle = stl.style().setFontSize(16).bold();
		StyleBuilder subTitleStyle = stl.style().setFontSize(10)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		StyleBuilder columnHeaderStyle = stl.style().setBorder(stl.pen1Point()).setPadding(5)
				.setForegroundColor(Color.WHITE).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
				.setBackgroundColor(Color.decode("#008000")).bold();
		StyleBuilder columnStyle = stl.style().setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		reportTemplate.setLocale(Locale.getDefault()).setColumnStyle(columnStyle).setColumnTitleStyle(columnHeaderStyle)
				.highlightDetailEvenRows().crosstabHighlightEvenRows();

		builder.setTemplate(reportTemplate);

		String path = dadosHeader.getLogo().replace("\\", "\\\\");

		builder.title(cmp.horizontalList()
				.add(cmp.horizontalList(cmp.image(path).setFixedDimension(80, 80), cmp.horizontalGap(10),
						cmp.verticalList(
								cmp.text(dadosHeader.getNomeEmpresa()).setStyle(mainHeaderStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
								cmp.text(dadosHeader.getEndereco()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
								cmp.text(dadosHeader.getCidade()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
								cmp.text(dadosHeader.getTelefone()).setStyle(subTitleStyle)
										.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)),
						cmp.horizontalGap(10))));

		builder.title(cmp.horizontalList(cmp.verticalList(cmp.verticalGap(10), cmp.line(), cmp.verticalGap(10))));

		builder.setDataSource(orcamento.getItens()).columns(quantidadeColumn, unidadeColumn, descricaoColumn,
				valorUnitarioColumn, subtotalColumn);

		try {
			builder.toPdf(export.pdfExporter(filePath));
			return 1;
		} catch (DRException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
