package mat.shared.measure.measuredetails.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NumeratorExclusionsModel extends MeasureDetailsRichTextAbstractModel implements IsSerializable{

	public NumeratorExclusionsModel() {
		super("", "");
	}
	
	public NumeratorExclusionsModel(NumeratorExclusionsModel model) {
		super(model.getPlainText(), model.getFormattedText());
	}
	
	public void accept(MeasureDetailsModelVisitor measureDetailsModelVisitor) {
		measureDetailsModelVisitor.visit(this);
	}
}
