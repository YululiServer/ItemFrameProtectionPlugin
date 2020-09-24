package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ItemFrameData {
    private final Plugin plugin;

    private List<FrameData> frameList = new ArrayList<>();
    private List<DropData> dropList = new ArrayList<>();


    public ItemFrameData(Plugin plugin){
        this.plugin = plugin;
    }

    public List<FrameData> getItemFrameList(){
        return frameList;
    }

    public FrameData getItemFrame(UUID createUser, UUID itemFrame){
        for (FrameData frameData : frameList) {
            if (frameData.getCreateUser().equals(createUser) && frameData.getItemFrame().equals(itemFrame)) {
                return frameData;
            }
        }

        return null;
    }

    public FrameData getItemFrame(UUID itemFrame){
        for (FrameData frameData : frameList) {
            if (frameData.getItemFrame().equals(itemFrame)) {
                return frameData;
            }
        }

        return null;
    }

    public void addFrameList(FrameData data){
        frameList.add(data);
    }

    public void delFrameList(FrameData data){
        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    if (frameList.size() < 1){
                        break;
                    }

                    if (i < frameList.size()){
                        if (frameList.get(i).getCreateUser().equals(data.getCreateUser()) && frameList.get(i).getItemFrame().equals(data.getItemFrame())){
                            frameList.remove(i);
                            break;
                        }
                    } else {
                        break;
                    }
                    i++;
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);
    }

    public List<DropData> getDropList(){
        if (dropList == null){
            return new ArrayList<>();
        }
        return dropList;
    }

    public DropData getDropDataByItem(UUID dropItem){
        for (DropData dropData : dropList) {
            if (dropData.getItemUUID().equals(dropItem)) {
                return dropData;
            }
        }

        return null;
    }

    public List<DropData> getDropDataByUser(UUID dropUser){

        List<DropData> data = new ArrayList<>();
        for (DropData dropData : dropList) {
            if (dropData.getDropUser().equals(dropUser)) {
                data.add(dropData);
            }
        }

        return data;
    }

    public void addDropList(DropData data){
        dropList.add(data);
    }

    public void delDropList(DropData data){
        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 0;
                int count = -1;
                while (true){
                    if (dropList.size() < 1){
                        break;
                    }

                    if (i < dropList.size()){
                        if (dropList.get(i) != null && dropList.get(i).getDropUser() != null && dropList.get(i).getItemUUID() != null && dropList.get(i).getDropUser().equals(data.getDropUser()) && dropList.get(i).getItemUUID().equals(data.getItemUUID())){
                            count = i;
                            break;
                        }
                    } else {
                        break;
                    }
                    i++;
                }

                if (count != -1){
                    dropList.remove(count);
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);
    }
}
