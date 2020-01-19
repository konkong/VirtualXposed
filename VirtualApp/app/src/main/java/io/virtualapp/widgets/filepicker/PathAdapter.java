package io.virtualapp.widgets.filepicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lody.virtual.helper.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import io.virtualapp.R;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {
    public interface OnItemClickListener {
        void click(int position);
    }

    public interface OnCancelChoosedListener {
        void cancelChoosed(CheckBox checkBox);
    }

    private List<File> mListData;
    private Context mContext;
    public OnItemClickListener onItemClickListener;
    private FileFilter mFileFilter;
    private boolean[] mCheckedFlags;
    private boolean mMutilyMode;

    public PathAdapter(List<File> mListData, Context mContext, FileFilter mFileFilter, boolean mMutilyMode) {
        this.mListData = mListData;
        this.mContext = mContext;
        this.mFileFilter = mFileFilter;
        this.mMutilyMode = mMutilyMode;
        mCheckedFlags = new boolean[mListData.size()];
    }

    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.file_picker_listitem, null);
        PathViewHolder pathViewHolder = new PathViewHolder(view);
        return pathViewHolder;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(final PathViewHolder holder, final int position) {
        final File file = mListData.get(position);
        if (file.isFile()) {
            updateFileIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            holder.tvDetail.setText(mContext.getString(R.string.file_picker_FileSize) + " " + FileUtils.getReadableFileSize(file.length()));
            holder.cbChoose.setVisibility(View.VISIBLE);
        } else {
            updateFloaderIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            //文件大小过滤
            List files = null;
            try{files = FileUtils.getFileList(file.getAbsolutePath(), mFileFilter);}catch (Exception ex){}
            if (files == null) {
                holder.tvDetail.setText("0 " + mContext.getString(R.string.file_picker_LItem));
            } else {
                holder.tvDetail.setText(files.size() + " " + mContext.getString(R.string.file_picker_LItem));
            }
            holder.cbChoose.setVisibility(View.GONE);
        }
        if (!mMutilyMode) {
            holder.cbChoose.setVisibility(View.GONE);
        }
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isFile()) {
                    holder.cbChoose.setChecked(!holder.cbChoose.isChecked());
                }
                onItemClickListener.click(position);
            }
        });
        holder.cbChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同步复选框和外部布局点击的处理
                onItemClickListener.click(position);
            }
        });
        holder.cbChoose.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        holder.cbChoose.setChecked(mCheckedFlags[position]);//用数组中的值设置CheckBox的选中状态
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.cbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckedFlags[position] = b;
            }
        });
    }

    private void updateFloaderIconStyle(ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.ic_file_picker_folder_style_blue);
    }

    private void updateFileIconStyle(ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.ic_file_picker_style_blue);
    }

    /**
     * 设置监听器
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置数据源
     *
     * @param mListData
     */
    public void setmListData(List<File> mListData) {
        this.mListData = mListData;
        mCheckedFlags = new boolean[mListData.size()];
    }

    /**
     * 设置是否全选
     *
     * @param isAllSelected
     */
    public void updateAllSelelcted(boolean isAllSelected) {

        for (int i = 0; i < mCheckedFlags.length; i++) {
            mCheckedFlags[i] = isAllSelected;
        }
        notifyDataSetChanged();
    }

    class PathViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutRoot;
        private ImageView ivType;
        private TextView tvName;
        private TextView tvDetail;
        private CheckBox cbChoose;

        public PathViewHolder(View itemView) {
            super(itemView);
            ivType = (ImageView) itemView.findViewById(R.id.iv_type);
            layoutRoot = (RelativeLayout) itemView.findViewById(R.id.layout_item_root);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_detail);
            cbChoose = (CheckBox) itemView.findViewById(R.id.cb_choose);
        }
    }
}


