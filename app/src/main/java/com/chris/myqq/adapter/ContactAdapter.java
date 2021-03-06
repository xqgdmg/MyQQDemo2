package com.chris.myqq.adapter;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.chris.myqq.R;
import java.util.ArrayList;

import com.chris.myqq.util.StringUtil;


/**
 * 联系人
 *
 */
public class ContactAdapter extends BaseAdapter implements SectionIndexer{
    private ArrayList<String> contacts;
    private SparseIntArray intArray = new SparseIntArray();// SparseIntArray的用法
    private SparseIntArray intArrayB = new SparseIntArray();
    public ContactAdapter(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public String getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置用户名
        holder.contact_item_name.setText(contacts.get(position));

        //根据首字母，判断当前是否要显示分割
        String currentC = StringUtil.getFirstC(contacts.get(position));
        holder.contact_item_sectionName.setText(currentC);

        // 和上一个联系人比较首字母，显示字母的横条
        if (position != 0 && currentC.equals(StringUtil.getFirstC(contacts.get(position - 1)))){
            holder.contact_item_sectionName.setVisibility(View.GONE);
        }else {
            holder.contact_item_sectionName.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * SlidBar 中的方法
     */
    @Override
    public String[] getSections() {
        //清空intArray
        intArray.clear();
        intArrayB.clear();
        //定义集合存放sections
        ArrayList<String> sections = new ArrayList<String>();
        //遍历联系人集合添加section
        for (int i = 0; i < contacts.size(); i++) {
            //获取联系人首字母
            String firstC = StringUtil.getFirstC(contacts.get(i));
            if(!sections.contains(firstC)){
                sections.add(firstC);
                //添加section和position对应的值
                intArray.put(sections.size()-1,i);
            }
            intArrayB.put(i,sections.size()-1);
        }
        return sections.toArray(new String[sections.size()]);
    }

    /**
     * SlidBar 中的方法
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        return intArray.get(sectionIndex);
    }

    /**
     * ？？？
     */
    @Override
    public int getSectionForPosition(int position) {
        return intArrayB.get(position);
    }

    class ViewHolder {

        private final TextView contact_item_sectionName;
        private final TextView contact_item_name;

        public ViewHolder(View view) {
            contact_item_sectionName = (TextView) view.findViewById(R.id.contact_item_sectionName);
            contact_item_name = (TextView) view.findViewById(R.id.contact_item_name);

        }
    }
}
