package org.churuata.rest.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruata;

@Entity
public class Presentation implements IPresentation{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Churuata churuata;

	private String title;
		
	@Basic(optional = true)
	private String description;

	private PresentationTypes type;
	
	private String link;

	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public Presentation() {
		super();
	}

	public Presentation( IChuruata churuata,  PresentationTypes type, String presentation, String link) {
		this( churuata, type, presentation, link, null );
	}
	
	public Presentation( IChuruata churuata, PresentationTypes type, String presentation, String link, String description) {
		super();
		this.churuata = (Churuata) churuata;
		this.title = presentation;
		this.description = description;
		this.type = type;
		this.link = link;
		this.createDate = Calendar.getInstance().getTime();
	}

	@Override
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setPresentation(String presentation) {
		this.title = presentation;
	}

	@Override
	public PresentationTypes getType() {
		return type;
	}

	public void setType(PresentationTypes type) {
		this.type = type;
	}
	
	@Override
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}
	
	@Override
	public Map<String, String> toAttributes(){
		Map<String, String> results = new HashMap<>();
		results.put(IPresentation.Attributes.TITLE.name(), title);
		results.put(IPresentation.Attributes.DESCRIPTION.name(), description);
		results.put(IPresentation.Attributes.LINK.name(), link);
		results.put(IPresentation.Attributes.TYPE.name(), type.name());
		return results;
	}
}