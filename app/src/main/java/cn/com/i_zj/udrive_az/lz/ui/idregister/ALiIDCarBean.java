package cn.com.i_zj.udrive_az.lz.ui.idregister;

/**
 * @author JayQiu
 * @create 2018/10/31
 * @Describe 阿里云解析身份证正反面
 */
public class ALiIDCarBean {
    private String start_date;
    private String end_date;
    private String issue;

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
