package net.net16.xhoemawn.suicideprevention.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.ReportActivity;
import net.net16.xhoemawn.suicideprevention.model.Report;
import net.net16.xhoemawn.suicideprevention.tools.ReportType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by xhoemawn12 on 7/1/17.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportHolder> {

    private LinkedHashMap<String, Report> reportLinkedHashMap;
    private ArrayList<Report> reports;
    private ArrayList<String> keys;

    public ReportAdapter(LinkedHashMap<String, Report> reportLinkedHashMap) {
        this.reportLinkedHashMap = reportLinkedHashMap;
    }

    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportHolder(v);
    }

    @Override
    public void onBindViewHolder(ReportHolder holder, int position) {
        Report report = reports.get(position);

        holder.reportType.setText(printReportType(report.getReportType()));
        holder.reportDescripttion.setText(report.getReportDescription());
    }

    private String printReportType(int reportType) {
        String str = new String();
        str = "Others";
        if (reportType == ReportType.IMPOLITE) {
            str = "Impolite";
        } else if (reportType == ReportType.PORNOGRAPHIC_CONTENT) {
            str = "Pornographic Content";
        } else if (reportType == ReportType.SPAM) {
            str = "Spam";
        }
        return str;
    }

    @Override
    public int getItemCount() {
        reports = new ArrayList<>(reportLinkedHashMap.values());
        Collections.reverse(reports);
        keys = new ArrayList<>(reportLinkedHashMap.keySet());
        Collections.reverse(keys);
        return reportLinkedHashMap.size();
    }

    class ReportHolder extends RecyclerView.ViewHolder {
        private TextView reportType;
        private TextView reportDescripttion;

        ReportHolder(View itemView) {
            super(itemView);
            reportType = (TextView) itemView.findViewById(R.id.reportType);
            reportDescripttion = (TextView) itemView.findViewById(R.id.reportedDescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReportActivity.class);
                    intent.putExtra("REPORTID", keys.get(getAdapterPosition()));
                    intent.putExtra("REPORT", reports.get(getAdapterPosition()));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

}
