package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DAO.ReportMapper;
import com.example.demo.Entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReportService {
    @Autowired
    private ReportMapper reportMapper;
    public void addNewReport(Report report){
        report.setIsCheck(false);
        report.setFailReason(null);
        reportMapper.insert(report);
    }
    //如果不填写failReason则视作举报不成功
    public void checkReportById(int id,int checkUserId,String failReason){
        Report.ReportBuilder reportBuilder = Report.builder().isCheck(true).id(id).checkUserId(checkUserId);
        if (failReason != null&& ! failReason.isBlank()) {
            reportBuilder.failReason(failReason);
        }
        reportMapper.updateById(reportBuilder.build());
    }
    public List<Report> showAllReport(int current, int size){
      return reportMapper.selectPage(new Page<>(current,size), null).getRecords();
    }
    public List<Report> showAllReportUnchecked(int current,int size){
        return reportMapper.selectPage(new Page<>(current,size  ), new QueryWrapper<Report>().eq("is_check", false)).getRecords();
    }
    public List<Report> showAllReportChecked(int current,int size){
        return reportMapper.selectPage(new Page<>(current,size), new QueryWrapper<Report>().eq("is_check", true)).getRecords();
    }
    public List<Report> showAllReportFailed(int current,int size) {
        return reportMapper.selectPage(new Page<>(current,size), new QueryWrapper<Report>().eq("is_check", true).isNull("fail_reason")).getRecords();}
    public List<Report> showAllReportByUserId(int current, int size, int userId){
        return reportMapper.selectPage(new Page<>(current,size), new QueryWrapper<Report>().eq("informer_id", userId)).getRecords();
    }
}
