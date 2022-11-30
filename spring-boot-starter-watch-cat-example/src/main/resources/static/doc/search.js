let api = [];
const apiDocListSize = 3
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'ConvertTestController',
    order: '1',
    link: '请求参数转换工具测试',
    desc: '请求参数转换工具测试',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: '/convert/strToLocalDate/{a}',
    desc: '',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: '/convert/strToLocalDateTime/{a}',
    desc: '',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: '/convert/strToLocalTime/{a}',
    desc: '',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: '/convert/strToAreaDetail',
    desc: '',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: '/convert/jsonStrToLocalDateTime',
    desc: '这里实际上没有用到 convert 的功能，纯靠在接收对象中增加注解（@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd")）实现',
});
api[0].list.push({
    alias: 'SecretCatTestController',
    order: '2',
    link: '请求响应加解密工具测试',
    desc: '请求响应加解密工具测试',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/example/secretCatExample/case1',
    desc: '',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/example/secretCatExample/case2',
    desc: '',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: '/example/secretCatExample/case4',
    desc: '',
});
api[0].list.push({
    alias: 'TestController',
    order: '3',
    link: '',
    desc: '',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/example/sensitiveCatExample',
    desc: '',
});
api[0].list.push({
    alias: 'ValidatorController',
    order: '4',
    link: '',
    desc: '',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/validator/case1',
    desc: '验证域名',
});
api[0].list.push({
    alias: 'LogCatTestController',
    order: '5',
    link: '日志工具测试',
    desc: '日志工具测试',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/log-cat/case1',
    desc: '',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/log-cat/case2',
    desc: '',
});
api[0].list.push({
    alias: 'ResultTestController',
    order: '6',
    link: '请求详情对象封装测试',
    desc: '请求详情对象封装测试',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: '/result/result/{a}',
    desc: '',
});
api[0].list.push({
    alias: 'LimitCatTestController',
    order: '7',
    link: '业务频率控制工具测试',
    desc: '业务频率控制工具测试',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: '/limit-cat/case1',
    desc: '非异常触发',
});
api[0].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: '/limit-cat/case2',
    desc: '异常触发',
});
api[0].list[6].list.push({
    order: '3',
    deprecated: 'false',
    url: '/limit-cat/case3',
    desc: '异常+code 触发',
});
api[0].list[6].list.push({
    order: '4',
    deprecated: 'false',
    url: '/limit-cat/case4',
    desc: '异常+code(指定code来源字段) 触发',
});
api[0].list[6].list.push({
    order: '5',
    deprecated: 'false',
    url: '/limit-cat/case5',
    desc: '自定义异常提示',
});
api[0].list[6].list.push({
    order: '6',
    deprecated: 'false',
    url: '/limit-cat/case6',
    desc: '异常触发（多个 LimitCat 同时使用）',
});
api[0].list[6].list.push({
    order: '7',
    deprecated: 'false',
    url: '/limit-cat/case7',
    desc: '场景（scene）为空，取方法名',
});
api[0].list.push({
    alias: 'SignCatTestController',
    order: '8',
    link: '请求验签响应加签工具测试',
    desc: '请求验签响应加签工具测试',
    list: []
})
api[0].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: '/example/signCatExample/case1',
    desc: '',
});
api.push({
    name: '入口功能组',
    order: '2',
    list: []
})
api.push({
    name: '业务功能组',
    order: '3',
    list: []
})
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    if (apiGroups.length > 0) {
        if (apiDocListSize === 1) {
            let apiData = apiGroups[0].list;
            let order = apiGroups[0].order;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+order+'_'+apiData[j].order+'_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                let doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated === 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_'+order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+apiGroup.order+'_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    let doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated === 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}