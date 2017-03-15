package model;

public class DescriptorMetaData {
	private String descriptorName;	
	private String[] attributesName;

	public DescriptorMetaData(String descriptorName, String[] attributesName) {
		this.descriptorName = descriptorName;
		this.attributesName = attributesName;
	}

	public String getDescriptorName() {
		return descriptorName;
	}

	public String[] getAttributesName() {
		return attributesName;
	}

}
