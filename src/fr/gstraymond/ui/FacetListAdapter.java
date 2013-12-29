package fr.gstraymond.ui;

import static android.R.style.TextAppearance_DeviceDefault_Large_Inverse;
import static android.R.style.TextAppearance_DeviceDefault_Medium;
import static android.R.style.TextAppearance_DeviceDefault_Medium_Inverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.constants.FacetConst;
import fr.gstraymond.magicsearch.model.response.facet.Facet;
import fr.gstraymond.magicsearch.model.response.facet.Term;


public class FacetListAdapter extends BaseAdapter {

	private static final int HOLO_BLUE = Color.rgb(51, 181, 229);
	private List<Term> terms;
	private Map<String, Facet> facets;
	private List<Term> selectedTerms;

	public FacetListAdapter(Map<String, Facet> facets, SearchOptions options) {
		this.facets = facets;
		this.selectedTerms = new ArrayList<Term>();
		this.terms = new ArrayList<Term>();

		if (facets != null) {
			for (String facetAsString : FacetConst.getFacetOrder()) {
				Facet facet = facets.get(facetAsString);
				if (facet != null) {
					List<Term> facetTerms = facet.getTerms();
					
					if (! facetTerms.isEmpty()) {
						terms.add(buildTermClass(facetAsString));
						terms.addAll(facetTerms);
						
						List<String> termsAsString = options.getFacets().get(facetAsString);
						if (termsAsString != null) {
							selectedTerms.addAll(findTerms(termsAsString, facetTerms));
						}
					}
				}
			}
		}
	}
	
	private List<Term> findTerms(List<String> termsAsString, List<Term> terms) {
		List<Term> termsFound = new ArrayList<Term>();
		
		for (String termAsString : termsAsString) {
			Term termFound = findTerm(termAsString, terms);
			if (termFound != null) {
				termsFound.add(termFound);
			}
		}
		
		return termsFound;
	}
	
	private Term findTerm(String termAsString, List<Term> terms) {
		for(Term term : terms) {
			if (term.getTerm().equals(termAsString)) {
				return term;
			}
		}
		return null;
	}

	private Term buildTermClass(String name) {
		Term term = new Term();
		term.setTerm(name);
		term.setCount(-1);
		return term;
	}

	@Override
	public int getCount() {
		return terms.size();
	}

	@Override
	public Object getItem(int position) {
		return terms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TextView textView = new TextView(parent.getContext());
		textView.setGravity(Gravity.CENTER);
		Term term = terms.get(position);
		
		if (term.getCount() == -1) {
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextAppearance(parent.getContext(), TextAppearance_DeviceDefault_Large_Inverse);
			String text = FacetConst.getFacetName(term.getTerm(), parent.getContext());
			textView.setText(text);
		} else {
			if (selectedTerms.contains(term)) {
				textView.setBackgroundColor(HOLO_BLUE);
				textView.setTextAppearance(parent.getContext(), TextAppearance_DeviceDefault_Medium_Inverse);
			} else {
				textView.setTextAppearance(parent.getContext(), TextAppearance_DeviceDefault_Medium);
			}
			
			String text = term.getTerm() + " (" + term.getCount() + ")";
			textView.setText(text);
		}
		return textView;
	}

	public Term getTerm(int position) {
		return terms.get(position);
	}

	public String getFacet(Term term) {
		for (Map.Entry<String, Facet> facet : facets.entrySet()) {
			if (facet.getValue().getTerms().contains(term)) {
				return facet.getKey();
			}
		}
		return null;
	}
	
	public boolean isTermSelected(Term term) {
		return selectedTerms.contains(term);
	}
}
