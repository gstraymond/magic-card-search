package fr.gstraymond.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.constants.FacetConst;
import fr.gstraymond.magicsearch.model.response.facet.Facet;
import fr.gstraymond.magicsearch.model.response.facet.Term;


public class FacetListAdapter extends BaseExpandableListAdapter {

	private List<String> facetList;
	private List<String> selectedFacets;
	private Map<String, Facet> facetMap;
	private List<Term> selectedTerms;

	public FacetListAdapter(Map<String, Facet> facets, SearchOptions options) {
		this.facetMap = facets;
		this.selectedFacets = new ArrayList<String>();
		this.facetList = new ArrayList<String>();
		this.selectedTerms = new ArrayList<Term>();

		if (facets == null) {
			return;
		}
		
		for (String facetAsString : FacetConst.getFacetOrder()) {
			Facet facet = facets.get(facetAsString);
			if (facet == null) {
				continue;
			}
			facetList.add(facetAsString); 
			List<Term> facetTerms = facet.getTerms();
			
			if (! facetTerms.isEmpty()) {
				List<String> termsAsString = options.getFacets().get(facetAsString);
				if (termsAsString != null) {
					selectedFacets.add(facetAsString);
					selectedTerms.addAll(findTerms(termsAsString, facetTerms));
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

	private List<Term> getChildren(int groupPosition) {
		return facetMap.get(getGroup(groupPosition)).getTerms();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return getChildren(groupPosition, childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            LayoutInflater inflater = getLayoutInflater(parent.getContext());
            view = inflater.inflate(R.layout.drawer_child, null);
        }
        
		Term term = getChildren(groupPosition, childPosition);
		String text = term.getTerm();

		TextView textTextView = (TextView) view.findViewById(R.id.drawer_child_text);
		TextView counterTextViewInactive = (TextView) view.findViewById(R.id.drawer_child_counter_inactive);
		counterTextViewInactive.setVisibility(View.GONE);
		TextView counterTextViewActive = (TextView) view.findViewById(R.id.drawer_child_counter_active);
		counterTextViewActive.setVisibility(View.GONE);
		
		TextView counterTextView = counterTextViewInactive;
		
		if (selectedTerms.contains(term)) {
			counterTextView = counterTextViewActive;
			textTextView.setText(Html.fromHtml("<b>" + text + "</b>", null, null));
		} else {
			textTextView.setText(text);
		}
		counterTextView.setVisibility(View.VISIBLE);
		counterTextView.setText(term.getCount() + "");
		return view;
	}

	private Term getChildren(int groupPosition, int childPosition) {
		return getChildren(groupPosition).get(childPosition);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getChildren(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return facetList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return facetList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            LayoutInflater inflater = getLayoutInflater(parent.getContext());
            view = inflater.inflate(R.layout.drawer_group, null);
        }
		
        TextView textView = (TextView) view.findViewById(R.id.drawer_group_textview);
		String facet = facetList.get(groupPosition);
		
		if (selectedFacets.contains(facet)) {
			ExpandableListView expandableListView = (ExpandableListView) parent;
		    expandableListView.expandGroup(groupPosition);
		}
		
		textView.setText(FacetConst.getFacetName(facet, parent.getContext()));
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public String getFacet(Term term) {
		for (Map.Entry<String, Facet> facet : facetMap.entrySet()) {
			if (facet.getValue().getTerms().contains(term)) {
				return facet.getKey();
			}
		}
		return null;
	}
	
	public boolean isTermSelected(Term term) {
		return selectedTerms.contains(term);
	}
	
	private LayoutInflater getLayoutInflater(Context context) {
		return LayoutInflater.from(context);
	}
}
