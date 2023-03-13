package com.cat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合转树形工具类（20220506，性能比自己封装的好）
 *
 * @author hudongshan
 */
public class TreeUtils<T> {

	private Function<T, Object> parent;
	private Function<T, Object> code;
	private Function<T, List<T>> children;

	public TreeUtils<T> parent(Function<T, Object> parent) {
		this.parent = parent;
		return this;
	}

	public TreeUtils<T> code(Function<T, Object> code) {
		this.code = code;
		return this;
	}

	public TreeUtils<T> children(Function<T, List<T>> children) {
		this.children = children;
		return this;
	}

	/**
	 * 集合转树形
	 * 不指定父节点，则会按照当前集合查询，查询到当前集合没有找到父节点的则为父节点
	 * */
	public List<T> tree(List<T> sourceList) {
		List<T> treeList = new ArrayList<>();
		Map<Object, T> codeMap = sourceList.stream().collect(Collectors.toMap(t->code.apply(t), t -> t));
		sourceList.stream().forEach(t -> {
			if(!codeMap.containsKey(parent.apply(t))){
				treeList.add(t);
			} else {
				T parentT = codeMap.get(parent.apply(t));
				children.apply(parentT).add(t);
			}
		});
		return  treeList;
	}

	/**
	 * 集合转树形
	 * 指定父节点，只查询指定父节点下的数据节点
	 * */
	public List<T> tree(List<T> sourceList, Object pid){
		List<T> treeList = new ArrayList<>();
		Map<Object, T> codeMap = sourceList.stream().collect(Collectors.toMap(t->code.apply(t), t -> t));
		sourceList.stream().forEach(t -> {
			if(Objects.equals(pid,parent.apply(t))){
				treeList.add(t);
			} else {
				T parentT = codeMap.get(parent.apply(t));
				if(Objects.nonNull(parentT)){
					children.apply(parentT).add(t);
				}
			}
		});
		return  treeList;
	}
//
//	@Data
//	static class Menu {
//		private Long id;
//		private Long pid;
//		private String name;
//		private String url;
//		private String delStatus;
//		private List<Menu> menus;
//
//		public Menu(Long id, Long pid, String name, String url, String delStatus) {
//			this.id = id;
//			this.pid = pid;
//			this.name = name;
//			this.url = url;
//			this.delStatus = delStatus;
//		}
//	}
//
//	private static List<Menu> menuList = new ArrayList<Menu>(20){{
//		add(new Menu(1L,0L,"一级菜单","一级路由","0"));
//		add(new Menu(2L,1L,"二级菜单","二级路由","0"));
//		add(new Menu(3L,1L,"二级菜单","二级路由","0"));
//		add(new Menu(4L,2L,"三级菜单","三级路由","0"));
//		add(new Menu(5L,2L,"三级菜单","三级路由","0"));
//		add(new Menu(6L,0L,"一级菜单","一级路由","0"));
//		add(new Menu(7L,3L,"三级菜单","三级路由","0"));
//		add(new Menu(8L,5L,"四级菜单","四级路由","0"));
//		add(new Menu(9L,7L,"四级菜单","四级路由","0"));
//		add(new Menu(10L,6L,"二级菜单","二级路由","0"));
//		add(new Menu(11L,6L,"二级菜单","二级路由","0"));
//		add(new Menu(12L,10L,"三级菜单","三级路由","0"));
//		add(new Menu(13L,11L,"三级菜单","三级路由","0"));
//		add(new Menu(14L,2L,"三级菜单","三级路由","0"));
//		add(new Menu(15L,5L,"四级菜单","四级路由","0"));
//		add(new Menu(16L,3L,"三级菜单","三级路由","0"));
//		add(new Menu(17L,6L,"二级菜单","二级路由","0"));
//		add(new Menu(18L,8L,"五级菜单","五级路由","0"));
//		add(new Menu(19L,10L,"三级菜单","三级路由","0"));
//		add(new Menu(20L,15L,"五级菜单","五级路由","0"));
//	}};
//
//	public static void main(String[] args) {
//		TimeInterval timer = DateUtil.timer();
//		for (int i = 0; i < 1000000; i++) {
//			List<Menu> tree = new TreeUtils<Menu>()
//					.code(Menu::getId)
//					.parent(Menu::getPid)
//					.children(menu -> {
//						if (menu.menus == null) {
//							menu.menus = new ArrayList<>();
//						}
//						return menu.menus;
//					})
//					.tree(menuList);
//		}
//		System.out.println(timer.interval());//花费毫秒数
//	}


}