package lifetime.suumo;

import android.widget.BaseExpandableListAdapter;

/**
 * Created by MyPC on 26/09/2017.
 */

public class Expandable_list_Adapter extends BaseExpandableListAdapter implements OnCheckedChangeListener {

    private Context context;
    private ArrayList<String> groupNames;
    private ArrayList<ArrayList<String>> child;
    private LayoutInflater inflater;

    public Expandable_list_Adapter(Context context,
                                   ArrayList<String> groupNames,
                                   ArrayList<ArrayList<String>> child ) {
        this.context = context;
        this.groupNames= groupNames;
        this.child = child;
        inflater = LayoutInflater.from( context );
    }

    public Object getChild(int groupPosition, int childPosition) {
        return child.get( groupPosition ).get( childPosition );
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.child_row, parent, false);
        String c = (String)getChild( groupPosition, childPosition );
        TextView color = (TextView)v.findViewById( R.id.childname );
        if( color != null )
            color.setText( c );

        CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );

        //cb.setChecked(false);
        cb.setOnCheckedChangeListener(this);
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return child.get( groupPosition ).size();
    }

    public Object getGroup(int groupPosition) {
        return groupNames.get( groupPosition );
    }

    public int getGroupCount(){
        return groupNames.size();
    }
    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false);
        String gt = (String)getGroup( groupPosition );
        TextView colorGroup = (TextView)v.findViewById( R.id.childname );
        if( gt != null )
            colorGroup.setText( gt );
        CheckBox cb = (CheckBox)v.findViewById( R.id.check2 );
        cb.setChecked(false);
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        Log.e("is group checked","group "+groupPosition);
        Log.e("selectable","has" +childPosition);
        return true;
    }

    public void onGroupCollapsed (int groupPosition) {}
    public void onGroupExpanded(int groupPosition) {}

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

    }
    public void isChecked(){

    }

}
