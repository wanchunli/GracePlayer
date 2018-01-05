package com.wan.grace.graceplayer.utils;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.manager.AppContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/10.
 */
public class GetBaiduLocation {

    private LocationService locationService;

    private LocationClient locationClient = null;
    private int UPDATE_TIME = 5000;
    private Map<String, Object> map = new HashMap<String, Object>();

    public GetBaiduLocation(Context context) {
//        getLoction(context);
        getNewLoction(context);
    }

    public void getLoction(Context context) {
        if (locationClient == null) {
            locationClient = new LocationClient(context);
            //设置定位条件
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);        //是否打开GPS
            option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
//            option.setPriority(LocationClientOption.NetWorkFirst);    //设置定位优先级
//            option.setProdName("LocationGrace");    //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
            option.setScanSpan(0);    //设置定时定位的时间间隔。单位毫秒
            option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
            option.setIsNeedLocationDescribe(true);
            locationClient.setLocOption(option);
        }
        //注册位置监听器
        locationClient.registerNotifyLocationListener(new BaiDuBDLocationListener(context));
//        locationClient.registerLocationListener(new BaiDuBDLocationListener(context));
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

    public void getNewLoction(Context context) {
        // -----------location config ------------
        locationService = ((AppContext) context).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
//        int type = getIntent().getIntExtra("from", 0);
//        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
//        } else if (type == 1) {
//            locationService.setLocationOption(locationService.getOption());
//        }
        locationService.start();// 定位SDK
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

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Log.i("locat",sb.toString());
            }
        }
    };



}
