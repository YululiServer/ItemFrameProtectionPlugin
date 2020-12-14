package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DropItem;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.ItemFrameProtect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Protect {

    // キャッシュ用
    private List<ItemFrameProtect> frameProtectCacheList = Collections.synchronizedList(new ArrayList<>());
    private List<DropItem> dropItemCacheList = Collections.synchronizedList(new ArrayList<>());

    Protect(){



    }

}
