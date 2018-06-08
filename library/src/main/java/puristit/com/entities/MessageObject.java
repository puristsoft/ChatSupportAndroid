package puristit.com.entities;

import java.security.PublicKey;

import puristit.com.utilities.Utils;

/**
 * Created by Anas on 2/25/2018.
 */

public class MessageObject {


    private String body;
    private String created_at;
    private long timestamp;
    private String sent_by;
    private String operator_id;
    private String operator_jid;
    private String end_customer_id;
    private String end_customer_jid;
    private String platform;
    private String file_url;
    private String file_name;
    private String thumbnail_path;
    private Sender sender;


    public String getBody() {
        return body;
    }

    public MessageObject setBody(String body) {
        this.body = body;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public MessageObject setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public String getSent_by() {
        return sent_by;
    }

    public MessageObject setSent_by(String sent_by) {
        this.sent_by = sent_by;
        return this;
    }


    public Sender getSender() {
        if (getOperator_id() != null && !getOperator_id().isEmpty()){
            sender = Sender.Operator;
        } else {
            sender = Sender.Mine;
        }
        return sender;
    }


    public String getOperator_id() {
        return operator_id;
    }

    public MessageObject setOperator_id(String operator_id) {
        this.operator_id = operator_id;
        return this;
    }

    public String getOperator_jid() {
        return operator_jid;
    }

    public MessageObject setOperator_jid(String operator_jid) {
        this.operator_jid = operator_jid;
        return this;
    }

    public String getEnd_customer_id() {
        return end_customer_id;
    }

    public MessageObject setEnd_customer_id(String end_customer_id) {
        this.end_customer_id = end_customer_id;
        return this;
    }

    public String getEnd_customer_jid() {
        return end_customer_jid;
    }

    public MessageObject setEnd_customer_jid(String end_customer_jid) {
        this.end_customer_jid = end_customer_jid;
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public MessageObject setPlatform(String platform) {
        this.platform = platform;
        return this;
    }


    public long getTimestamp() {
        return timestamp * 1000;
    }

    public MessageObject setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }


    public String getFile_url() {
        return file_url;
    }

    public MessageObject setFile_url(String file_url) {
        this.file_url = file_url;
        return this;
    }

    public String getFile_name() {
        return file_name;
    }

    public MessageObject setFile_name(String file_name) {
        this.file_name = file_name;
        return this;
    }

    public String getThumbnail_path() {
        return thumbnail_path;
    }

    public MessageObject setThumbnail_path(String thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
        return this;
    }

    public enum Sender{
        Mine,
        Operator
    }
}
