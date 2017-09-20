package net.android.jn.model;

/**
 * Created by clickapps on 16/6/15.
 */
public final class ProfileModel {


    private String id, first_name, timezone, locale, name, last_name, gender, email;
    private Picture picture;
    private Cover cover;
    public String ids;

    public Picture getPicture() {
        return picture;
    }

    public Cover getCover() {
        return cover;
    }

    public String getId() {
        ids = id;
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public class Picture {
        private Data data;

        public Data getData() {
            return data;
        }
    }

    public class Cover {
        private String id, source, offset_y;

        public String getId() {
            return id;
        }

        public String getSource() {
            return source;
        }

        public String getOffset_y() {
            return offset_y;
        }
    }

    public class Data {
        private String is_silhouette, url;

        public String getIs_silhouette() {
            return is_silhouette;
        }

        public String getUrl() {
            return url;
        }
    }
}
