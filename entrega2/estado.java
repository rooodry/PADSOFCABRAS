public enum estado {
    perfecto("perfecto"),
    muy_bueno("muy bueno"),
    bueno("bueno"),
    uso_ligero("uso ligero"),
    uso_evidente("uso evidente"),
    muy_usado("muy usado"),
    dañado("dañado");

    private String desc;

    private estado(String desc) {
        this.desc = desc;
    }
    
    public String getDesc() {
        return desc;
    }
}
