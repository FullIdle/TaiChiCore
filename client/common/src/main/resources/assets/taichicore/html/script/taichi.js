console.log("use taichi.js")
//cef请求的封装
window.taichi = function (
    params,
    {
        onSuccess = () => {},
        onFailure = (code, msg) => { console.error(code, msg) },
        persistent = false
    } = {}
) {
    const queryId = window.cefQuery({
        request: 'taichicore:' + JSON.stringify(params),
        persistent: persistent,
        onSuccess: onSuccess,
        onFailure: onFailure
    });

    return {
        queryId: queryId,
        cancel: function () {
            window.cefQueryCancel(queryId);
        }
    };
};

window.taichiElements = {
    renders: {}
}

customElements.define("taichi-render", class extends HTMLElement {
    /** @type {CanvasRenderingContext2D|null} */
    _ctx = null;
    _id = null;

    constructor() {
        super();
    }

    connectedCallback() {
        if (!this.hasAttribute("type")) return;

        const type = this.getAttribute("type");
        const width = this.getAttribute("width") || "256";
        const height = this.getAttribute("height") || "256";
        const scale = this.getAttribute("scale") || "1.0";
        const rotX = this.getAttribute("rotX") || "0.0";
        const rotY = this.getAttribute("rotY") || "0.0";
        const rotZ = this.getAttribute("rotZ") || "0.0";

        const canvas = document.createElement("canvas");
        canvas.width = parseInt(width);
        canvas.height = parseInt(height);
        canvas.style.cssText = "width:100%;height:100%;";
        this.append(canvas);
        this._ctx = canvas.getContext("2d");

        taichi({
            action: "renderNotice",
            isRegister: true,
            type: type,
            width: width,
            height: height,
            scale: scale,
            rotX: rotX,
            rotY: rotY,
            rotZ: rotZ
        }, {
            onSuccess: (id) => {
                this._id = id;
                window.taichiElements.renders[id] = this;
            }
        });
    }

    disconnectedCallback() {
        if (!this._id) return;
        taichi({
            action: "renderNotice",
            isRegister: false,
            id: this._id
        });
        delete window.taichiElements.renders[this._id];
    }

    update(base64) {
        if (!this._ctx) return;
        const img = new Image();
        img.onload = () => {
            this._ctx.clearRect(0, 0, this._ctx.canvas.width, this._ctx.canvas.height);
            this._ctx.drawImage(img, 0, 0);
        };
        img.src = "data:image/png;base64," + base64;
    }
});