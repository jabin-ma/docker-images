package top.misec.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import top.misec.api.ApiList;
import top.misec.config.ConfigLoader;
import top.misec.utils.HttpUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * GetVideoId .
 *
 * @author @JunzhouLiu
 * @since 2020/11/12 13:17
 */
@Data
public class GetVideoId {
    private ArrayList<String> followUpVideoList;
    private ArrayList<String> rankVideoList;
    private ArrayBlockingQueue<String> followUpVideoQueue;

    public GetVideoId() {
        this.followUpVideoList = queryDynamicNew();
        this.rankVideoList = regionRanking();
        if (this.followUpVideoList.size() > 0) {
            this.followUpVideoQueue = new ArrayBlockingQueue<>(followUpVideoList.size());
            this.followUpVideoQueue.addAll(followUpVideoList);
        }
    }

    public void updateAllVideoList() {
        this.followUpVideoList = queryDynamicNew();
        this.rankVideoList = regionRanking();
        if (this.followUpVideoList.size() > 0) {
            this.followUpVideoQueue = new ArrayBlockingQueue<>(followUpVideoList.size());
            this.followUpVideoQueue.addAll(followUpVideoList);
        }
    }

    /**
     * 从动态中获取随机bv号.
     */
    public String getFollowUpRandomVideoBvid() {
        if (followUpVideoList.size() == 0) {
            return getRegionRankingVideoBvid();
        }
        Random random = new Random();
        return followUpVideoList.get(random.nextInt(followUpVideoList.size()));
    }

    /**
     * 暂未启用的方法.
     *
     * @return 从阻塞队列中获取bv号
     */
    @ExtensionMethod
    public String getFollowUpRecentVideoBvid() {
        return followUpVideoQueue.peek() == null ? getRegionRankingVideoBvid() : followUpVideoQueue.poll();
    }

    /**
     * 排行榜获取随机bv号.
     */
    public String getRegionRankingVideoBvid() {
        Random random = new Random();
        return rankVideoList.get(random.nextInt(rankVideoList.size()));
    }

    public ArrayList<String> queryDynamicNew() {
        ArrayList<String> arrayList = new ArrayList<>();
        String urlParameter = "?uid=" + ConfigLoader.helperConfig.getBiliVerify().getDedeUserId()
                + "&type_list=8"
                + "&from="
                + "&platform=web";
        JsonObject jsonObject = HttpUtils.doGet(ApiList.QUERY_DYNAMIC_NEW + urlParameter);
        JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("cards");

        if (jsonArray != null) {
            for (JsonElement videoInfo : jsonArray) {
                JsonObject tempObject = videoInfo.getAsJsonObject().getAsJsonObject("desc");
                arrayList.add(tempObject.get("bvid").getAsString());
            }
        }
        return arrayList;
    }

    /**
     * 从有限分区中随机返回一个分区rid.
     *
     * @return regionId 分区id
     */
    public int randomRegion() {
        int[] arr = {1, 3, 4, 5, 36, 188, 160, 22, 119};
        return arr[(int) (Math.random() * arr.length)];
    }

    /**
     * 默认请求动画区，3日榜单.
     */
    public ArrayList<String> regionRanking() {
        int rid = randomRegion();
        int day = 3;
        return regionRanking(rid, day);
    }

    /**
     * 请求分区.
     *
     * @param rid 分区id 默认为3
     * @param day 日榜，三日榜 周榜 1，3，7
     * @return 随机返回一个aid
     */
    public ArrayList<String> regionRanking(int rid, int day) {

        ArrayList<String> videoList = new ArrayList<>();
        String urlParam = "?rid=" + rid + "&day=" + day;
        JsonObject resultJson = HttpUtils.doGet(ApiList.GET_REGION_RANKING + urlParam);

        JsonArray jsonArray = resultJson.getAsJsonArray("data");

        if (jsonArray != null) {
            for (JsonElement videoInfo : jsonArray) {
                JsonObject tempObject = videoInfo.getAsJsonObject();
                videoList.add(tempObject.get("bvid").getAsString());
            }
        }
        return videoList;
    }

    public void videoUpdate(String mid) {
        String urlParam = "?mid=" + mid + "&ps=30&tid=0&pn=1&keyword=&order=pubdate&jsonp=jsonp";
        JsonObject resultJson = HttpUtils.doGet(ApiList.GET_BVID_BY_CREATE + urlParam);
        JsonArray jsonArray = resultJson.getAsJsonObject("data").getAsJsonObject("list").getAsJsonArray("vlist");

        if (jsonArray != null) {
            for (JsonElement videoInfo : jsonArray) {
                String bvid = videoInfo.getAsJsonObject().get("bvid").getAsString();
                // int play = videoInfo.getAsJsonObject().get("play").getAsInt();
                if (CoinAdd.isCoinAdded(bvid)) {
                    this.rankVideoList.add(bvid);
                    this.followUpVideoList.add(bvid);
                }
            }
        }
    }
}
