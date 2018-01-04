package com.wan.grace.graceplayer.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/10.
 */
public class GetBaiduLocation {
    private LocationClient locationClient = null;
    private int UPDATE_TIME = 5000;
    private Map<String, Object> map = new HashMap<String, Object>();

    public GetBaiduLocation(Context context) {
        getLoction(context);
    }

    public void getLoction(Context context) {
        if (locationClient == null) {
            locationClient = new LocationClient(context);
            //设置定位条件
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);        //是否打开GPS
            option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
//            option.setPriority(LocationClientOption.NetWorkFirst);    //设置定位优先级
//            option.setProdName("LocationDemo");    //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
            option.setScanSpan(0);    //设置定时定位的时间间隔。单位毫秒
            option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
            option.setIsNeedLocationDescribe(true);
            locationClient.setLocOption(option);
        }
        //注册位置监听器
        locationClient.registerLocationListener(new BaiDuBDLocationListener(context));
        locationClient.start();
         /*
                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
					 *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
					 *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
					 *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
					 *定时定位时，调用一次requestLocation，会定时监听到定位结果。
					 */
        locationClient.requestLocation();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    class BaiDuBDLocationListener implements BDLocationListener {
        Context context;

        public BaiDuBDLocationListener(Context context) {
            this.context = context;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
//
//            if (!checkNull(location.getLongitude() + "") && !checkNull(location.getLatitude() + "")) {
//                LogUtils.i("longitude=" + location.getLongitude() + ",latitude=" + location.getLatitude());
//                LogUtils.i("address=" + location.getAddrStr() + ",province=" + location.getProvince());
//                LogUtils.i("district=" + location.getDistrict());
//            } else {
//                getLoction(context);
//            }
            map.put("locType", location.getLocType() + "");
            map.put("longitude", location.getLongitude() + "");//经度106.72
            map.put("latitude", location.getLatitude() + "");//纬度26.57
            map.put("addressStr", location.getAddrStr() + "");//详细的能用的
            map.put("address", location.getAddress() + "");//不能用
            map.put("province", location.getProvince() + "");
            map.put("district", location.getDistrict() + "");//地区
        }
    }


    public boolean checkNull(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
