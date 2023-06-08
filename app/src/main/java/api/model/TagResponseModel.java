package api.model;

import java.util.List;

public class TagResponseModel {
    public long id;
    public List<TagIDModel> tags;

    public TagResponseModel(long photoId, List<TagIDModel> listOfTags) {
        this.id = photoId;
        this.tags = listOfTags;
    }
}

