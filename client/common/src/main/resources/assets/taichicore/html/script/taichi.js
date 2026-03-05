console.log("use taichi.js")
//cef请求的封装
window.taichi = function(params) {
    return new Promise((resolve, reject) => {
        window.cefQuery({
            request: 'taichicore:' + JSON.stringify(params),
            onSuccess: resolve,
            onFailure: (code, msg) => reject({code, msg})
        });
    });
};

//之后的内容
customElements.define("taichi-render-player", class extends HTMLElement {
    /** @type {CanvasRenderingContext2D|null} */
    _ctx = null;

    constructor() {
        super();
    }

    connectedCallback() {
        const canvas = document.createElement("canvas");
        this.append(canvas)
        this._ctx = canvas.getContext("2d");
        console.log("hello world test")
    }

    update(data) {
        if (!this._ctx) return;
        const img = new Image();
        img.onload = () => {
            this._ctx.clearRect(0, 0, this._ctx.canvas.width, this._ctx.canvas.height);
            this._ctx.drawImage(img, 0, 0);
        };
        img.src = "data:image/png;base64," + data;
    }
})