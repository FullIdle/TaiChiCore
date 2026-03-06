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
    _canvas = null;
    _mutationObserver = null;

    constructor() {
        super();
    }

    connectedCallback() {
        if (!this.hasAttribute("type")) return;

        const type = this.getAttribute("type");
        // 画布分辨率，在type为player时候，同时也是minecraft侧渲染的宽高大小
        const width = this.getAttribute("width") || "256";
        const height = this.getAttribute("height") || "256";

        const canvas = document.createElement("canvas");
        canvas.width = parseInt(width);
        canvas.height = parseInt(height);
        this._canvas = canvas;

        // 先同步 class、style、id 到 canvas
        canvas.className = this.className;
        this.className = "";
        canvas.style.cssText = this.style.cssText;
        if (this.id) {
            canvas.id = this.id;
            this.removeAttribute("id");
        }

        // 最后设置外层消失
        this.style.display = "contents";

        // 实时同步属性变化
        this._mutationObserver = new MutationObserver(() => {
            if (this.className) {
                canvas.className += " " + this.className;
                this.className = "";
            }
            if (this.id) {
                canvas.id = this.id;
                this.removeAttribute("id");
            }
        });
        this._mutationObserver.observe(this, {
            attributes: true,
            attributeFilter: ["class", "style", "id"]
        });

        this.appendChild(canvas);
        this._ctx = canvas.getContext("2d");

        const params = {
            action: "renderNotice",
            isRegister: true,
            type: type
        };

        switch (type) {
            case "player": {
                params.width = width;
                params.height = height;
                params.scale = this.getAttribute("scale") || "1.0";
                break;
            }
            case "item": {
                params.slot = this.getAttribute("slot") || "0";
                params.size = this.getAttribute("size") || "64";
                break;
            }
        }

        taichi(params, {
            onSuccess: (id) => {
                this._id = id;
                console.log("set id: " + id);
                window.taichiElements.renders[id] = this;
            }
        });
    }

    disconnectedCallback() {
        if (this._mutationObserver) {
            this._mutationObserver.disconnect();
            this._mutationObserver = null;
        }
        if (!this._id) return;
        taichi({
            action: "renderNotice",
            isRegister: false,
            id: this._id
        });
        console.log("delete id: " + this._id);
        delete window.taichiElements.renders[this._id];
    }

    update(base64) {
        if (!this._ctx) return;
        const img = new Image();
        if (!base64) {
            this._ctx.clearRect(0, 0, this._ctx.canvas.width, this._ctx.canvas.height);
            return;
        }
        img.onload = () => {
            this._ctx.clearRect(0, 0, this._ctx.canvas.width, this._ctx.canvas.height);
            this._ctx.drawImage(img, 0, 0);
        };
        img.src = "data:image/png;base64," + base64;
    }
});