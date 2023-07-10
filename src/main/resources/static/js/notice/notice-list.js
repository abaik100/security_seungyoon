function getWriteButton(){
	const listFooter = document.querySelector(".list-footer");
	
	if(getUser() != null) {
		if(getUser().userRoles.includes("ROLE_ADMIN")) {
			listFooter.innerHTML +=`
				<button type= "button" class="notice-add-button">글쓰기</button>
			`
			const noticeAddButton = documnet.querySelector(".notice-add-button");
			noticeAddButton.onclick = () => {
				location.href = "/notice/addition";
			}
		}
	}
}
getWriteButton();