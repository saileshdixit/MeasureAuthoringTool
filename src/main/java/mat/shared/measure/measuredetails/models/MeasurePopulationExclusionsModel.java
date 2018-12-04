package mat.shared.measure.measuredetails.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MeasurePopulationExclusionsModel extends MeasureDetailsRichTextAbstractModel implements IsSerializable{

	public MeasurePopulationExclusionsModel() {
		super("", "");
	}
	
	public MeasurePopulationExclusionsModel(MeasurePopulationExclusionsModel model) {
		super(model.getPlainText(), model.getFormattedText());
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void accept(MeasureDetailsModelVisitor measureDetailsModelVisitor) {
		measureDetailsModelVisitor.visit(this);
	}
}
