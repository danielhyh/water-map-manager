// src/utils/leaflet-draw-zh.js
import L from 'leaflet'

// 汉化Leaflet.draw
if (L.drawLocal) {
    L.drawLocal.draw = {
        toolbar: {
            actions: {
                title: '取消绘制',
                text: '取消'
            },
            finish: {
                title: '完成绘制',
                text: '完成'
            },
            undo: {
                title: '删除最后一个绘制的点',
                text: '删除最后一个点'
            },
            buttons: {
                polyline: '绘制线条',
                polygon: '绘制多边形',
                rectangle: '绘制矩形',
                circle: '绘制圆形',
                marker: '添加标记',
                circlemarker: '添加圆形标记'
            }
        },
        handlers: {
            circle: {
                tooltip: {
                    start: '点击并拖动以绘制圆形'
                },
                radius: '半径'
            },
            circlemarker: {
                tooltip: {
                    start: '点击地图添加圆形标记'
                }
            },
            marker: {
                tooltip: {
                    start: '点击地图添加标记'
                }
            },
            polygon: {
                tooltip: {
                    start: '点击开始绘制多边形',
                    cont: '继续点击绘制多边形',
                    end: '点击第一个点闭合多边形'
                }
            },
            polyline: {
                error: '<strong>错误:</strong> 线段边缘不能交叉!',
                tooltip: {
                    start: '点击开始绘制线条',
                    cont: '继续点击绘制线条',
                    end: '点击最后一个点结束绘制'
                }
            },
            rectangle: {
                tooltip: {
                    start: '点击并拖动绘制矩形'
                }
            },
            simpleshape: {
                tooltip: {
                    end: '释放鼠标完成绘制'
                }
            }
        }
    };

    L.drawLocal.edit = {
        toolbar: {
            actions: {
                save: {
                    title: '保存修改',
                    text: '保存'
                },
                cancel: {
                    title: '取消编辑，放弃所有修改',
                    text: '取消'
                },
                clearAll: {
                    title: '清除所有图层',
                    text: '清除所有'
                }
            },
            buttons: {
                edit: '编辑图层',
                editDisabled: '没有可编辑的图层',
                remove: '删除图层',
                removeDisabled: '没有可删除的图层'
            }
        },
        handlers: {
            edit: {
                tooltip: {
                    text: '拖动控制点或标记修改要素',
                    subtext: '点击取消撤销修改'
                }
            },
            remove: {
                tooltip: {
                    text: '点击要删除的要素'
                }
            }
        }
    };
}

export default L