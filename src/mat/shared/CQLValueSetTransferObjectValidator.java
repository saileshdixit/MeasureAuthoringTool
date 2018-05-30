package mat.shared;

import java.util.List;

import mat.model.CQLValueSetTransferObject;
import mat.model.cql.CQLQualityDataSetDTO;

public class CQLValueSetTransferObjectValidator {
	public boolean isValid(CQLValueSetTransferObject object) {
		boolean isValid = true;
		if(object.getMatValueSet() != null){
			if(StringUtility.isEmptyOrNull(object.getMatValueSet().getDisplayName())) {
				isValid = false;
			} else if((StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getProgram()) && StringUtility.isEmptyOrNull(object.getCqlQualityDataSetDTO().getRelease())) || (StringUtility.isEmptyOrNull(object.getCqlQualityDataSetDTO().getProgram()) && StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getRelease()))) {
				isValid = false;
			} else if((StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getProgram()) || StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getRelease())) && object.isVersion()){
				isValid = false;
			}
		} else if(object.getUserDefinedText().trim().isEmpty()){
			isValid = false;
		} else {
			if(StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getProgram()) || StringUtility.isNotBlank(object.getCqlQualityDataSetDTO().getRelease()) || object.isVersion()){
				isValid = false;
			}
		}
		
		List<CQLQualityDataSetDTO> existingQDSList = object.getAppliedQDMList();
		for (CQLQualityDataSetDTO dataSetDTO : existingQDSList) {
			if (dataSetDTO.getOid().equalsIgnoreCase(ConstantMessages.USER_DEFINED_QDM_OID) && dataSetDTO
					.getName().equalsIgnoreCase(object.getCqlQualityDataSetDTO().getName())) {
				isValid = false;
				break;
			}
		}
		
		return isValid;
	}
}
